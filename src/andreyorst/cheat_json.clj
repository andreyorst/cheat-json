(ns andreyorst.cheat-json
  (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io])
  (:import
   (java.io Reader StringReader)
   (java.lang StringBuilder)))

(defn parse
  "Takes a file or reader and traverses, removing unnecessary spaces and
  colons (`:`) after keys.  Afterwards the result is parsed as EDN."
  [file]
  (with-open [^Reader rdr (io/reader file)]
    (loop [in-string? false
           escape? false
           ^StringBuilder res (StringBuilder.)]
      (let [c (.read rdr)]
        (if (>= c 0)
          (let [c (char c)]
            (case c
              \" (if in-string?
                   (recur escape? false (.append res c))
                   (recur true false (.append res c)))
              \\ (recur in-string? (not escape?) (.append res c))
              \: (recur in-string? false (if in-string? (.append res c) res))
              (\space \newline \tab)
              (recur in-string? false (if in-string? (.append res c) res))
              (recur in-string? false (.append res c))))
          (edn/read-string (.toString res)))))))

(defn parse-string
  "Takes a string and traverses it, removing unnecessary spaces and
  colons (`:`) after keys.  Afterwards the result is parsed as EDN."
  [str]
  (parse (StringReader. str)))
