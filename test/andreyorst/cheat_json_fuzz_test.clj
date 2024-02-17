(ns andreyorst.cheat-json-fuzz-test
  (:require [andreyorst.cheat-json :as cheat]
            [andreyorst.json-gen :refer [json-object]]
            [clojure.test :refer [testing is]]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :as prop]
            [clojure.data.json :as json]))

(defspec ^:fuzz parse-test 200
  (testing "Generate random json object, and parse it."
    (prop/for-all [edn json-object]
      (let [json (json/write-str edn)]
        (is (= edn (cheat/read-string json)))))))
