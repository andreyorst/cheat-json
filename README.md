# Cheat JSON

> *If only `edn/read-string` was faster*

A library that cheats and parses JSON as EDN.

Here's a typical JSON:

```json
{
  "foo": "bar",
  "baz": [
    {"quux": 42}
  ]
}
```

And here's an EDN, describing the same thing:

```clojure
{
  "foo" "bar",
  "baz" [
    {"quux" 42}
  ]
}
```

See the difference?

This library simply reads a stream of bytes and removes all `:` outside of the strings, then parses the result as EDN with `clojure.edn/read-string`.
This means that some things aren't parsed conventionally, e.g. `null` in JSON is parsed as a symbol, and not as `nil`.
No JSON validation or error handling, in general, is handled by this library.

## Wait, that's illegal!

Yes!
And also kinda slow.
I'd advise against using it too.

## License

Copyright © 2022 Andrey Listopadov

Distributed under the Eclipse Public License version 1.0.
