(ns cls.report-generation
  (:require [org.httpkit.server :as server]
            [hiccup.core :refer [html]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojure.data.json :as json]
            [clojure.data.xml :refer :all])
  (:gen-class))

(defn simple-body-page [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (html [:html
                   [:head
                    [:title "hello everyone"]]
                   [:body
                    [:p "Hello World"]
                    [:p [:a {:href (str "/read-xml?file=123")}
                         "open xml file number"]]

                    [:p [:a {:href (str "/request")}
                         "request"]]
                    [:p [:a {:href (str "/request?foo=bar&num=123")}
                         "request with parameters"]]]])})

(defn request-example [req]
  (let [rkeys (keys req)
        dat (map (fn [e]
                   [:p [:span {:style "color: red"} (str(key e) " ")]
                    (val e)])
                 req)]
    {:status  200
     :headers {"Content-Type" "text/html"}
     :body   (html
              [:html
               [:head
                [:title "hello"]]
               [:body
                [:div
                 [:p {:style "margin: 1em; padding: 1em; background: #cfc;"}
                  (str "Request Object: " req)]
                 [:div
                  {:style "padding: 1em; background: yellow; margin: 1em; padding: 1em;"}
                  dat]
                 ]]])}))

(defn parsed-file [num]
  (let [xml-path  "/home/jacek/Desktop/LidlReports"
        xml-file (clojure.string/join [xml-path "/" (str num)])]
    (parse-str (slurp xml-file))))

(defn clean-content [x]
  (filter (fn [y] (not= (type y) java.lang.String)) (.content x)))

(defn example-descent []
  (-> (parsed-file 1366796)
      clean-content
      (nth 7) clean-content
      (nth 0)
      .attrs
      (get :customer_purchase_order)))

(defn read-xml [req]
  (let [params (get req :params)
        xml-path  "/home/jacek/Desktop/LidlReports"
        xml-file (clojure.string/join [xml-path "/" (get params :file)])]
    (print (str xml-file "\n"))
    {:status  200
     :headers {"Content-Type" "text/html"}
     :body    (html
               [:p "xml parsing will go here "]
               [:pre (str (parse-str (slurp xml-file)))])}))

(defroutes app-routes
  (GET "/" [] simple-body-page)
  (GET "/request" [] request-example)
  (GET "/read-xml" [] read-xml)
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
