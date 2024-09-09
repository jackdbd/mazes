{
  config,
  inputs,
  lib,
  pkgs,
  ...
}: {
  enterShell = ''
    hello
  '';

  enterTest = ''
    echo "assert Babashka version is 1.3.*"
    bb --version | ag "1\.3\.[0-9]+$"
    echo "assert neil version is 0.3.*"
    neil --version | ag "0\.3\.[0-9]+$"
  '';

  env.GREET = "devenv";

  # languages.clojure.enable = true;
  languages.nix.enable = true;

  # https://devenv.sh/packages/
  packages = with pkgs; [
    babashka # Clojure interpreter for scripting
    git
    neil # CLI to add common aliases and features to deps.edn-based projects
  ];

  # https://devenv.sh/pre-commit-hooks/
  pre-commit.hooks = {
    alejandra.enable = true; # Format Nix files
  };

  # https://devenv.sh/scripts/
  scripts.hello.exec = "echo hello from $GREET";

  scripts.versions.exec = ''
    echo "Versions"
    ${pkgs.babashka}/bin/bb --version
    ${pkgs.neil}/bin/neil --version
  '';
}
