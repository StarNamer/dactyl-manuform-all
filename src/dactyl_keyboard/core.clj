;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; The Dactyl-ManuForm Keyboard — Opposable Thumb Edition              ;;
;; Main Module — CLI, Final Composition and Outputs                    ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns dactyl-keyboard.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.pprint :refer [pprint]]
            [clj-yaml.core :as yaml]
            [scad-clj.scad :refer [write-scad]]
            [scad-clj.model :exclude [use import] :refer :all]
            [dactyl-keyboard.generics :as generics]
            [dactyl-keyboard.params :as params]
            [dactyl-keyboard.sandbox :as sandbox]
            [dactyl-keyboard.cad.aux :as aux]
            [dactyl-keyboard.cad.key :as key]
            [dactyl-keyboard.cad.body :as body]
            [dactyl-keyboard.cad.wrist :as wrist])
  (:gen-class :main true))

(defn new-scad []
  "Reload this namespace with any changed dependencies. Redraw .scad files."
  (clojure.core/use 'dactyl-keyboard.core :reload-all))

(defn metacluster [function getopt]
  "Apply passed function to all key clusters."
  (apply union (map (partial function getopt) key/clusters)))

(defn build-mcu-box [getopt]
  (difference 
    (union
      (if (= (getopt :mcu :support :style) :stop)
        (aux/mcu-stop getopt))
      (aux/connection-positive getopt)
      (if (getopt :case :back-plate :include) (aux/backplate-block getopt))
      (if (getopt :case :rear-housing :include) (body/rear-housing getopt)) 
      (aux/mcu-stop-fixture-positive getopt)
      (sandbox/positive getopt))
    (aux/connection-negative getopt)
    (aux/mcu-negative getopt)
    (if (getopt :case :back-plate :include)
      (aux/backplate-fastener-holes getopt))
    (sandbox/negative getopt)
    ))

(defn build-keyboard-right [getopt]
  "Right-hand-side keyboard model."
  (union
    (body/mask getopt
      (difference
        (union
          (metacluster key/cluster-plates getopt)
          (metacluster body/cluster-web getopt)
          (metacluster body/cluster-wall getopt)
          (if (getopt :wrist-rest :include)
            (case (getopt :wrist-rest :style)
              :solid (wrist/case-hook getopt)
              :threaded (wrist/case-plate getopt)))
          (if (= (getopt :mcu :support :style) :stop)
            (aux/mcu-stop getopt))
          (if (= (getopt :mcu :support :style) :stop)
            (aux/mcu-stop-fixture-positive getopt))
          (aux/connection-positive getopt)
          (aux/foot-plates getopt)
          (if (getopt :case :back-plate :include) (aux/backplate-block getopt))
          (if (getopt :case :rear-housing :include) (body/rear-housing getopt))
          (body/wall-tweaks getopt)
          (aux/mcu-lock-fixture-positive getopt)
          (sandbox/positive getopt))
        (metacluster key/cluster-cutouts getopt)
        (metacluster key/cluster-channels getopt)
        (aux/connection-negative getopt)
        (aux/mcu-negative getopt)
        (if (= (getopt :mcu :support :style) :lock)
          (aux/mcu-lock-sink getopt))
        (if (getopt :case :leds :include) (aux/led-holes getopt))
        (if (getopt :case :back-plate :include)
          (aux/backplate-fastener-holes getopt))
        (if (getopt :wrist-rest :include)
          (if (= (getopt :wrist-rest :style) :threaded)
            (wrist/threaded-fasteners getopt)))
        (sandbox/negative getopt))
      (if (= (getopt :switches :style) :mx)
        (metacluster key/cluster-nubs getopt))
      (if (= (getopt :mcu :support :style) :lock) ; Outside the alcove.
        (aux/mcu-lock-fixture-composite getopt)))
    ;; The remaining elements are visualizations for use in development.
    (if (getopt :keycaps :preview) (metacluster key/cluster-keycaps getopt))
    (if (getopt :mcu :preview) (aux/mcu-visualization getopt))
    (if (and (= (getopt :mcu :support :style) :lock)
             (getopt :mcu :support :preview))
      (aux/mcu-lock-bolt getopt))
    (if (and (getopt :wrist-rest :include) (getopt :wrist-rest :preview))
      (wrist/unified-preview getopt))))

(defn build-option-accessor [build-options]
  "Close over a user configuration."
  (letfn [(value-at [path] (get-in build-options path ::none))
          (path-exists? [path] (not (= ::none (value-at path))))
          (valid? [path] (and (path-exists? path)
                              (not (nil? (value-at path)))))  ; “false” is OK.
          (step [path key]
            (let [next-path (conj path key)]
             (if (path-exists? next-path) next-path path)))
          (backtrack [path] (reduce step [] path))]
    (fn [& path]
      (let [exc {:path path
                 :last-good (backtrack path)
                 :at-last-good (value-at (backtrack path))}]
        (if-not (path-exists? path)
          (throw (ex-info "Configuration lacks key"
                          (assoc exc :type :missing-parameter)))
          (if-not (valid? path)
            (throw (ex-info "Configuration lacks value for key"
                            (assoc exc :type :unset-parameter)))
            (value-at path)))))))

(defn enrich-option-metadata [build-options]
  "Derive certain properties that are implicit in the user configuration.
  Store these results under the “:derived” key in each section."
  (reduce
    (fn [coll [path callable]]
      (assoc-in coll (conj path :derived) (callable (build-option-accessor coll))))
    build-options
    ;; Mind the order. One of these may depend upon earlier steps.
    [[[:key-clusters :finger] (partial key/cluster-properties :finger)]
     [[:key-clusters :thumb] (partial key/cluster-properties :thumb)]
     [[:key-clusters :aux0] (partial key/cluster-properties :aux0)]
     [[:key-clusters] key/resolve-aliases]
     [[:keycaps] key/keycap-properties]
     [[:switches] key/keyswitch-dimensions]
     [[:case :rear-housing] body/housing-properties]
     [[:mcu] aux/derive-mcu-properties]
     [[:wrist-rest] wrist/derive-properties]]))

(defn build-all [build-options]
  "Make an option accessor function and write OpenSCAD files with it."
  (let [getopt (build-option-accessor (enrich-option-metadata build-options))
        scad-file (fn [filename model]
                    (spit (str "things/" filename ".scad") (write-scad model)))
        pair (fn [basename model]
               (scad-file (str "right-hand-" basename) model)
               (scad-file (str "left-hand-" basename) (mirror [-1 0 0] model)))]
   (scad-file "preview-keycap" (key/all-keycaps getopt))
   (pair "case" (build-keyboard-right getopt))
   (pair "mcu-box" (build-mcu-box getopt))
   (if (= (getopt :mcu :support :style) :lock)
     (scad-file "mcu-lock-bolt" (aux/mcu-lock-bolt getopt)))
   (if (getopt :wrist-rest :include)
     (do
       (pair "pad-mould" (wrist/rubber-casting-mould getopt))
       (pair "pad-shape" (wrist/rubber-insert getopt))
       (pair "plinth" (wrist/plinth-plastic getopt))))))

(def cli-options
  "Define command-line interface."
  [["-c" "--configuration-file PATH" "Path to parameter file in YAML format"
    :default ["resources/opt/default.yaml"]
    :assoc-fn (fn [m k new] (update-in m [k] (fn [old] (conj old new))))]
   [nil "--describe-parameters"
    "Print a Markdown document specifying what a configuration file may contain"]
   ["-d" "--debug"]
   ["-h" "--help"]])

(defn- from-file [filepath]
  (try
    (yaml/parse-string (slurp filepath))
    (catch java.io.FileNotFoundException _
      (do (println (format "Failed to load file “%s”." filepath))
          (System/exit 1)))))

(defn -main [& raw]
  (let [args (parse-opts raw cli-options)
        options (:options args)]
   (cond
     (some? (:errors args)) (do (println (first (:errors args)))
                                (println (:summary args))
                                (System/exit 1))
     (:help options) (println (:summary args))
     (:describe-parameters options) (params/print-markdown-documentation)
     :else
       (let [raws (apply generics/soft-merge
                    (map from-file (:configuration-file options)))]
        (if (:debug options) (do (println "Merged options:") (pprint raws)))
        (try
          (build-all (params/validate-configuration raws))
          (catch clojure.lang.ExceptionInfo e
            ;; Likely raised by getopt.
            (println "An exception occurred:" (.getMessage e))
            (pprint (ex-data e))
            (System/exit 1)))))))
