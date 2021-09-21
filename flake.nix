{
  description = "CIlib development environment";

  # This is nixpkgs release 20.09
  inputs.nixpkgs.url = "github:nixos/nixpkgs?rev=cd63096d6d887d689543a0b97743d28995bc9bc3";
  inputs.flake-utils.url = "github:numtide/flake-utils";

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let pkgs = nixpkgs.legacyPackages.${system}; in
      rec {
        packages.cilib = {};

        defaultPackage = packages.cilib;

        devShell = pkgs.mkShell {
          buildInputs = with pkgs; [
            openjdk11
            sbt
          ];
        };
      }
    );
}
