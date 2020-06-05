(defproject mazes "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.764"]
                 [org.clojure/spec.alpha "0.2.187"]
                 [quil "3.1.0"]]
  :main mazes.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all :uberjar-name "mazes-standalone.jar"}
             :dev {:dependencies [[io.aviso/pretty "0.1.37"]
                                  [pjstadig/humane-test-output "0.10.0"]]
                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]
                   :middleware [io.aviso.lein-pretty/inject]
                   :plugins [[com.jakemccrary/lein-test-refresh "0.24.1"]
                             [io.aviso/pretty "0.1.37"]
                             [jonase/eastwood "0.3.10"]
                             [lein-cljfmt "0.6.7" :exclusions [org.clojure/clojure]]]}})
