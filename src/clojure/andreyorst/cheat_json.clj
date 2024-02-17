(ns andreyorst.cheat-json
  (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io])
  (:import
   (andreyorst CheatReader)
   (java.io Reader StringReader PushbackReader))
  (:refer-clojure :exclude [read read-string]))

(defn
  ^{:added "0.2.3"}
  read
  "Takes a path to the file or a reader and creates a filter reader that
  removes unnecessary spaces and colons (`:`) after keys.  This reader
  is then used with the `clojure.edn/read` to read JSON as EDN."
  [reader]
  (with-open [^Reader rdr (io/reader reader)]
    (-> rdr
        CheatReader.
        PushbackReader.
        edn/read)))

(def ^{:added "0.1.0"
       :deprecated "0.2.3"}
  parse
  "Deprecated - use `read` instead."
  read)

(defn
  ^{:added "0.2.3"}
  read-string
  "Takes a JSON string and reads it as EDN, discarding unnecessary
  spaces and colons (`:`) after keys."
  [str]
  (read (StringReader. str)))

(def ^{:added "0.1.0"
       :deprecated "0.2.3"}
  parse-string
  "Deprecated - use `read-string` instead."
  read-string)
