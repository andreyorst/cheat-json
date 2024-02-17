(ns andreyorst.json-gen
  (:require
   [clojure.data.json :as json]
   [clojure.test.check.generators :as gen])
  (:import (java.io Writer StringWriter)))

(def json-base-type
  (gen/one-of
   [gen/string
    gen/small-integer
    (gen/double* {:NaN? false :infinite? false})
    gen/boolean]))

(def json-object
  (gen/map
   (gen/not-empty gen/string-ascii)
   (gen/recursive-gen
    (fn [inner]
      (gen/one-of [(gen/map (gen/not-empty gen/string-ascii) inner)
                   (gen/vector inner)]))
    json-base-type)))

(defn small-json
  "Generate a moderately sized JSON object.
  If `writer` is supplied, writes the object into it.  Otherwise,
  returns a string representing the object."
  ([]
   (let [w (StringWriter.)]
     (small-json w)
     (str w)))
  ([^Writer writer]
   (json/write (gen/generate json-object 100) writer)))

(defn big-json
  "Generate big amount of objects in a JSON array.
  If `writer` is supplied, writes the objects into it.  Otherwise,
  returns a string representing the object."
  ([]
   (let [w (StringWriter.)]
     (big-json w)
     (str w)))
  ([^Writer writer]
   (binding [*out* writer] (print "["))
   (dotimes [_ 1000]
     (small-json writer)
     (binding [*out* writer] (println ",")))
   (small-json writer)
   (binding [*out* writer] (println "]"))
   writer))
