(set-env!
  :source-paths    #{"src/clj" "src/shared"}
  :resource-paths  #{"resources"}
  :dependencies '[[org.omcljs/om               "1.0.0-alpha45"]
                  [bidi                        "2.0.10"         :exclusions [ring/ring-core]]
                  [org.clojure/clojurescript   "1.9.229"]
                  [kibu/pushy "0.3.6"]
                  [com.cognitect/transit-cljs  "0.8.239"]
                  [hiccup                      "1.0.5"]
                  [com.cognitect/transit-clj   "0.8.288"]
                  [org.clojure/tools.logging   "0.3.1"]
                  [org.slf4j/slf4j-log4j12     "1.7.21"]
                  [log4j/log4j                 "1.2.17"
                   :exclusions [javax.mail/mail javax.jms/jms
                                com.sun.jmdk/jmxtools com.sun.jmx/jmxri]]
                  [environ                     "1.1.0"]
                  [ring                        "1.6.0-beta5"]
                  [ring/ring-headers           "0.2.0"]
                  [com.datomic/datomic-free    "0.9.5394"
                   :exclusions [org.slf4j/slf4j-api org.slf4j/slf4j-nop
                                org.slf4j/slf4j-log4j12 org.slf4j/log4j-over-slf4j]]
                  [com.stuartsierra/component  "0.3.1"]

                  [org.danielsz/system         "0.3.2-SNAPSHOT"]
                  [boot-environ "1.1.0"]
                  [com.cemerick/piggieback     "0.2.1"          :scope "test"]
                  [adzerk/boot-cljs            "1.7.228-1"      :scope "test"]
                  [adzerk/boot-cljs-repl       "0.3.3"          :scope "test"]
                  [adzerk/boot-reload          "0.4.12"         :scope "test"]
                  [org.clojure/tools.nrepl     "0.2.12"         :scope "test"]
                  [org.clojure/tools.namespace "0.3.0-alpha3"   :scope "test"]
                  [weasel                      "0.7.0"          :scope "test"]])

(load-data-readers!)

(require
 '[adzerk.boot-cljs      :refer [cljs]]
 '[adzerk.boot-cljs-repl :as cr :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload    :refer [reload]]
 '[clojure.tools.namespace.repl :as repl]
 '[system.boot :refer [system run]]
 '[environ.boot :refer [environ]]
 '[todomvc.system :refer [dev-system]])

(deftask deps [])

(deftask data-readers []
    (fn [next-task]
      (fn [fileset]
        (#'clojure.core/load-data-readers)
        (with-bindings {#'*data-readers* (.getRawRoot #'*data-readers*)}
          (next-task fileset)))))

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
