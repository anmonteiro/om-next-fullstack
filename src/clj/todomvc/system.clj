(ns todomvc.system
  (:require todomvc.server
            [todomvc.datomic :as todomvc]
            [environ.core :refer [env]]
            [system.core :refer [defsystem]]
            [system.components.http-kit :refer [new-web-server]]))

(defsystem dev-system
  [:db (todomvc.datomic/new-database (env :db-uri))
   :webserver (new-web-server (env :web-port) (todomvc.server/todomvc-handler-dev))])
