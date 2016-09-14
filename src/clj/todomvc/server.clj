(ns todomvc.server
  (:require [clojure.java.io :as io]
            [clojure.walk :as walk]
            [todomvc.util :as util]
            [ring.util.response :refer [charset response file-response resource-response]]
            [ring.adapter.jetty :refer [run-jetty]]
            [todomvc.middleware
             :refer [wrap-transit-body wrap-transit-response
                     wrap-transit-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [bidi.bidi :as bidi]
            [com.stuartsierra.component :as component]
            [datomic.api :as d]
            [todomvc.datomic]
            [todomvc.page :as page]
            [om.next.server :as om]
            [todomvc.parser :as parser]
            [system.repl :refer [system]]))

;; =============================================================================
;; Routes

(def routes
  ["" {"/" :index
       "/api"
        {:get  {[""] :api}
         :post {[""] :api}}}])

;; =============================================================================
;; Handlers

(defn index [req]
  (assoc (charset (response (page/render-page req)) "utf-8")
    :headers {"Content-Type" "text/html"}))

(defn generate-response [data & [status]]
  {:status  (or status 200)
   :headers {"Content-Type" "application/transit+json"}
   :body    data})

(defn api [req]
  (let [data ((om/parser {:read parser/readf :mutate parser/mutatef})
                {:conn (:datomic-connection req)} (:transit-params req))
        data' (walk/postwalk (fn [x]
                               (if (and (sequential? x) (= :result (first x)))
                                 [(first x) (dissoc (second x) :db-before :db-after :tx-data)]
                                 x))
                data)]
    (generate-response data')))

;;;; PRIMARY HANDLER

(defn handler [req]
  (let [match (bidi/match-route routes (:uri req)
                :request-method (:request-method req))]
    (case (:handler match)
      :index (index req)
      :api   (api req)
      req)))

(defn wrap-connection [handler]
  (fn [req]
    (handler (assoc req :datomic-connection (-> system :db :connection)))))

(defn todomvc-handler []
  (wrap-resource
    (wrap-transit-response
      (wrap-transit-params (wrap-connection handler)))
    ""))

(defn todomvc-handler-dev []
  (fn [req]
    ((todomvc-handler) req)))
