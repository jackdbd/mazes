# mazes

Maze algorithms from Jamis Buck's book: [Mazes for Programmers](http://www.mazesforprogrammers.com/).

## Setup & Installation

This projects uses [Nix](https://nixos.org/) and [devenv](https://devenv.sh/) to define a reproducible developer environment.

You can install Nix and devenv by following the instructions in the [devenv documentation](https://devenv.sh/getting-started/).

If you have Nix and devenv installed, the developer environment will be automatically activated when you enter the repository root directory (thanks to direnv). You just need to run `direnv allow` to create the developer environment the first time you enter the repository root directory.

The Clojure dependencies of this project are declared in a [deps.edn](https://clojure.org/guides/deps_and_cli) file. You can install the developer environment, with all of its extra dependencies, using this command:

```sh
clj -A:dev
```

The `devenv.nix` file contains some tests to validate that the developer environment is the one you would expect. You can run these tests using the following command:

```sh
devenv test
```

The `bb.edn` file defines a few [babashka tasks](https://book.babashka.org/#tasks) that can be used to manage the project.

## Notes

Use Calva to [start a project REPL and jack-in](https://calva.io/connect/#jack-in-let-calva-start-the-repl-for-you), and pick `deps.edn` as the project type.

Print an ASCII art maze in the terminal:

```sh
bb maze:ascii
```
