# Cheat JSON

> *If only `edn/read` was faster*

A library that cheats and parses JSON as EDN.

Here's a typical JSON:

```json
{"foo": "bar", "baz": [{"quux": 42}]}
```

And here's an EDN, describing the same thing:

```clojure
{"foo" "bar", "baz" [{"quux" 42}]}
```

See the difference?

This library simply reads a stream of bytes and removes all `:` outside of the strings, then parses the result as EDN with `clojure.edn/read`.

## Wait, that's illegal!

Yes!
And also kinda slow.
Some benchmarks show that this library is slightly faster than `org.clojure/data.json` version `2.5.0`, but that's not the reason to use this library over any other implementation.
I'd advise against using it in general.

Moreover, some things aren't parsed conventionally, e.g. `null` in JSON is parsed as a symbol `null`, and not as `nil`.
Null handling can be implemented, but it requires more logic in the reader wrapper.
Apart from what the EDN parser does, no validation or error handling is done by this library.
A fun side effect of this approach, is that invalid JSON files, such as ones that are missing a colon or a comma are still parsed by this library.

## Development

The library implements a thin wrapper around any given reader that tracks the state of whether the parser is currently in string or not.
While outside the string, all `:` and spacious characters are ignored by the reader.
All other characters are fed to the EDN reader.
The wrapper is implemented in Java because doing the same via `proxy` is almost twice as slow, even with a mutable Java array holding state flags.

Before the library can be used in the REPL, the Java sources must be compiled with `clojure -T:build compile-java`.

The `:dev` alias provides some extra libraries, such as other JSON parsing libraries.

### Benchmarks

You can run the benchmarks across many libraries in the `andreyorst.cheat-json-benchmark` namespace.
Here are the results for parsing a 58M JSON on AMD Ryzen™ 7 3750H with Clojure 1.11.1, Java 17.0.9 via [criterium 0.4.6](https://clojars.org/criterium).
The JSON is generated from EDN, created with `clojure.test.check` generators, and transformed to JSON via the `clojure.data.json` library:

| Library/function                | Mean time     | Version | Commentary                                    |
|---------------------------------|---------------|---------|-----------------------------------------------|
| [cheshire][1].core/parse-stream | 21.292273 µs  | 5.12.0  |                                               |
| [jsonista][2].core/read-value   | 749.847236 ms | 0.3.8   |                                               |
| [charred][3].api/read-json      | 1.054495 sec  | 1.034   |                                               |
| [clj-json][4].core/parse-string | 1.115567 sec  | 0.5.3   | doesn't seem to have a stream parser          |
| [clojure][5].edn/read           | 1.974559 sec  | 1.11.1  | Reading raw EDN is faster than the conversion |
| [cheat-json][6]/read            | 2.790089 sec  | 0.2.3   | cheat-json converts JSON to EDN.              |
| [clojure.data.json][7]/read     | 3.682869 sec  | 2.5.0   |                                               |
| [pjson][8].core/read-str        | -             | 1.0.0   | can't handle the generated JSON               |

(All parsers produce the same EDN as a result.)

A more conventional, and less nested JSON files show a bit different results:

| Library/function           | Mean time ([64KB JSON][9]) | Mean time ([5MB JSON][10]) |
|----------------------------|---------------------------|----------------------------|
| cheshire.core/parse-stream | 57.137999 µs              | 54.927975 µs               |
| jsonista.core/read-value   | 683.855374 µs             | 53.730929 ms               |
| charred.api/read-json      | 711.244077 µs             | 54.599854 ms               |
| clj-json.core/parse-string | 299.507524 µs             | 27.764057 ms               |
| clojure.edn/read           | 2.506467 ms               | 209.648609 ms              |
| cheat-json/read            | 2.728934 ms               | 214.768154 ms              |
| clojure.data.json/read     | 1.827402 ms               | 154.400935 ms              |
| pjson.core/read-str        | 89.860981 µs              | 9.011588 ms                |


The difference between raw EDN `read` and Cheat-JSON wrapper is less dramatic.


## License

Copyright © 2022 Andrey Listopadov

Distributed under the Eclipse Public License version 1.0.

[1]: https://clojars.org/cheshire
[2]: https://clojars.org/metosin/jsonista
[3]: https://clojars.org/com.cnuernber/charred
[4]: https://clojars.org/clj-json
[5]: https://mvnrepository.com/artifact/org.clojure/clojure/1.11.1
[6]: https://github.com/andreyorst/cheat-json
[7]: https://mvnrepository.com/artifact/org.clojure/data.json
[8]: https://clojars.org/pjson
[9]: https://microsoftedge.github.io/Demos/json-dummy-data/64KB.json
[10]: https://microsoftedge.github.io/Demos/json-dummy-data/5MB.json
