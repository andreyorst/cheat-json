(ns andreyorst.cheat-json-test
  (:require
   [andreyorst.cheat-json :refer [parse parse-string]]
   [clojure.edn :as edn]
   [clojure.test :refer [deftest is testing]]))

(deftest parse-string-test
  (testing "Parsing various strings"
    (is (= {"foo" "bar"}
           (parse-string "{\"foo\": \"bar\"}")))
    (is (= {"foo" ["bar" "baz"]}
           (parse-string "{\"foo\": [\"bar\", \"baz\"]}")))))

(deftest parse-file-test
  (testing "Parsing valid files"
    (is (= (edn/read-string (slurp "test/data/valid1.edn"))
           (parse "test/data/valid1.json")))
    (is (= [[[[[[[[[[[[[[[[[[["Not too deep"]]]]]]]]]]]]]]]]]]]
           (parse "test/data/valid2.json")))
    (is (= {"JSON Test Pattern pass3"
            {"The outermost value" "must be an object or array.",
             "In this test" "It is an object."}}
           (parse "test/data/valid3.json")))))
