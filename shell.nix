{ pkgs }:

pkgs.mkShell {
  src = if pkgs.lib.inNixShell then null else pkgs.nix;
  buildInputs = with pkgs; [
    #openjdk11
    jdk8
    sbt
    #metals
  ];
}
