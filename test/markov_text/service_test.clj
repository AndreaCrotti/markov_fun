(ns markov-text.service-test
  (:require [markov-text.service :as service]
            [clojure.test :as t]
            [io.pedestal.test :refer [response-for]]
            [io.pedestal.http :as http]))

(def service
  (::http/service-fn (http/create-servlet service/service)))

(t/deftest home-page-test
  (t/is (=
         (:body (response-for service :get "/"))
         "Hello World again!")))

(t/deftest bible-quote-test
  (let [sample-quote (response-for service :get "/bible")]
    (t/is
     (=
      (:status sample-quote) 200))
    #_(t/is
     (pos? (count (:body sample-quote))))))
