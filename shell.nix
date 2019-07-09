let distro = fetchTarball https://github.com/NixOS/nixpkgs-channels/archive/nixos-19.03.tar.gz;
in with import distro {};
let myGHC = haskellPackages.ghcWithPackages
                     (haskellPackages: with haskellPackages; [
                       # libraries
                       parsek
                       # tools
                       cabal-install
                       mtl
                       split
                       logict
                       monadplus
                     ]);

in stdenv.mkDerivation {
  name = "dactyl-env";
  buildInputs = [ jdk leiningen openscad ];
}


