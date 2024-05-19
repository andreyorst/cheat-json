(ns andreyorst.cheat-json-benchmark
  (:require [criterium.core :as crit]
            [andreyorst.json-gen :as gen]
            [andreyorst.cheat-json :as cheat]
            [clojure.data.json :as json]
            [charred.api :as charred]
            [cheshire.core :as cheshire]
            [jsonista.core :as jsonista]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
            [pjson.core :as pjson]
            [clj-json.core :as clj-json]))

(defmacro ^:private annotate
  "Annotates the given expression with the callee name.
  Wraps the given expression with the given wrapper.

  `(annotate time (+ 1 2))` expands to `(time (+ 1 2))` and prints `+`
  before the output from the `time`."
  [wrapper expr]
  `(try
     (println '~(first expr))
     (~wrapper ~expr)
     nil
     (catch Exception e#
       (binding [*out* *err*]
         (println "Exception:" (ex-message e#))))))

(defmacro ^:private run-benchmarks
  "Accepts a `measure` macro, and a `file` to measure."
  [measure file]
  `(let [s# (slurp ~file)]
     (annotate ~measure (cheshire/parse-stream-strict (io/reader ~file)))
     (annotate ~measure (jsonista/read-value (io/reader ~file)))
     (annotate ~measure (charred/read-json (io/reader ~file)))
     (annotate ~measure (clj-json/parse-string s#))
     (annotate ~measure (cheat/read (io/reader ~file)))
     (annotate ~measure (json/read (io/reader ~file)))
     (annotate ~measure (pjson/read-str s#))))

(defn- quick
  "Reading a JSON file with different implementations.
  Quick measurement using ~`measure-macro`. Not really representative."
  [file]
  (run-benchmarks time file))

(defn- bench
  "Reading a JSON file with different implementations.
  Better measurement using the criterium library."
  [file]
  (run-benchmarks crit/quick-bench file))

(defn- parsers-agree
  "Check if all parsers produce the same EDN from the given JSON file."
  [file]
  (let [s (slurp file)]
    (= (cheshire/parse-stream-strict (io/reader file))
       (charred/read-json (io/reader file))
       (jsonista/read-value (io/reader file))
       (json/read (io/reader file))
       (clj-json/parse-string s)
       (cheat/read (io/reader file))
       (pjson/read-str s))))

(defn- compare-with-raw-edn
  "Since Cheat JSON is a thin reader wrapper around any given reader, it
  shouldn't be much slower than just reading EDN."
  [json-file edn-file]
  (annotate crit/quick-bench
            (edn/read (java.io.PushbackReader. (io/reader edn-file))))
  (annotate crit/quick-bench
            (cheat/read (io/reader json-file))))

(comment
  (def big (io/file "dev/data/big.json"))
  (with-open [w (io/writer big)] (gen/big-json w))

  (parsers-agree big)

  (quick big)
  (bench big)

  (def smal (io/file "dev/data/smal.json"))
  (with-open [w (io/writer smal)] (gen/small-json w))

  (parsers-agree smal)

  (quick smal)
  (bench smal)

  (def big-edn (io/file "dev/data/big.edn"))
  (spit big-edn (cheat/read big))
  (compare-with-raw-edn big big-edn)

  (def smal-edn (io/file "dev/data/smal.edn"))
  (spit smal-edn (cheat/read smal))
  (compare-with-raw-edn smal smal-edn)

  )
