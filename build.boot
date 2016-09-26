(set-env!
  :source-paths    #{"src/clj" "src/shared"}
  :resource-paths  #{"resources"}
  :dependencies '[[org.omcljs/om               "1.0.0-alpha46"
                   :exclusions [com.cognitect/transit-cljs]]
                  [bidi                        "2.0.11"
                   :exclusions [ring/ring-core prismatic/schema]]
                  [org.clojure/clojure         "1.9.0-alpha13"]
                  [org.clojure/clojurescript   "1.9.229"
                   :exclusions [org.clojure/clojure]]
                  [kibu/pushy "0.3.6"
                   :exclusions [org.clojure/clojure org.clojure/clojurescript]]
                  [com.cognitect/transit-cljs  "0.8.239"
                   :exclusions [org.clojure/clojure]]
                  [hiccup                      "1.0.5"
                   :exclusions [org.clojure/clojure]]
                  [com.cognitect/transit-clj   "0.8.288"]
                  [org.clojure/tools.logging   "0.3.1"
                   :exclusions [org.clojure/clojure]]
                  [org.slf4j/slf4j-log4j12     "1.7.21"]
                  [log4j/log4j                 "1.2.17"
                   :exclusions [javax.mail/mail javax.jms/jms
                                com.sun.jmdk/jmxtools com.sun.jmx/jmxri]]
                  [environ                     "1.1.0"
                   :exclusions [org.clojure/clojure]]
                  [ring                        "1.6.0-beta6"
                   :exclusions [org.clojure/clojure commons-codec]]
                  [ring/ring-headers           "0.3.0-beta1"
                   :exclusions [org.clojure/clojure ring/ring-core ;commons-fileupload commons-codec
                                ]]
                  [com.datomic/datomic-free    "0.9.5394"
                   :exclusions [org.slf4j/slf4j-api org.slf4j/slf4j-nop
                                org.slf4j/slf4j-log4j12 org.slf4j/log4j-over-slf4j
                                commons-codec com.google.guava/guava
                                org.clojure/clojure]]
                  [com.stuartsierra/component  "0.3.1"
                   :exclusions [org.clojure/clojure]]

                  [org.danielsz/system         "0.3.2-SNAPSHOT"
                   :exclusions [org.clojure/clojure]]
                  [boot-environ "1.1.0"]
                  [com.cemerick/piggieback     "0.2.1"          :scope "test"
                   :exclusions [org.clojure/clojure org.clojure/clojurescript]]
                  [adzerk/boot-cljs            "1.7.228-1"      :scope "test"]
                  [adzerk/boot-cljs-repl       "0.3.3"          :scope "test"]
                  [adzerk/boot-reload          "0.4.12"         :scope "test"]
                  [org.clojure/tools.nrepl     "0.2.12"         :scope "test"
                   :exclusions [org.clojure/clojure]]
                  [weasel                      "0.7.0"          :scope "test"
                   :exclusions [org.clojure/clojure org.clojure/clojurescript]]])

(load-data-readers!)

(require
 '[adzerk.boot-cljs      :refer [cljs]]
 '[adzerk.boot-cljs-repl :as cr :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload    :refer [reload]]
 '[system.boot :refer [system run]]
 '[environ.boot :refer [environ]]
 '[todomvc.system :refer [dev-system]])

(deftask deps [])

(deftask dev []
  (comp
    (environ :env {:db-uri   "datomic:mem://localhost:4334/todos"
                   :web-port 8081})
    (watch)
    (system :sys #'dev-system :auto true :files ["server.clj" "parser.clj"])
    (cljs-repl)
    (reload)
    (speak)
    (cljs :source-map true
          :compiler-options {:parallel-build true
                             :compiler-stats true}
          :ids #{"js/app"})
    (target)))
