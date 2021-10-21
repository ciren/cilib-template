{
  description = "CIlib development environment";

  inputs.nixpkgs.url = "github:nixos/nixpkgs/nixos-21.05";
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
