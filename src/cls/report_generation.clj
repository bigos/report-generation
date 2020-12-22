(ns cls.report-generation
  (:require [org.httpkit.server :as server]
            [hiccup.core :refer [html]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojure.data.json :as json])
  (:gen-class))

; Simple Body Page
(defn simple-body-page [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "Hello World"})

; request-example
(defn request-example [req]
  (let [rkeys (keys req)
        dat (map (fn [e]
                   [:p [:span {:style "color: red"} (str(key e) " ")]
                    (val e)])
                 req)]
    {:status  200
     :headers {"Content-Type" "text/html"}
     :body   (html [:div
                    [:p {:style "margin: 2em; background: #ccc;"}
                     (str "Request Object: " req)]
                    dat
                    ])}))

(defroutes app-routes
  (GET "/" [] simple-body-page)
  (GET "/request" [] request-example)
  (route/not-found "Error, page not found!"))

(defn -main
  "This is our main entry point"
  [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
                                        ; Run the server with Ring.defaults middleware
    (server/run-server (wrap-defaults #'app-routes site-defaults) {:port port})
                                        ; Run the server without ring defaults
                                        ;(server/run-server #'app-routes {:port port})
    (println (str "Running webserver at http:/127.0.0.1:" port "/"))))
