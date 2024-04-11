## 7.2

Introduced the ability to disable position tracking when parsing, to trade better performance for worse error messages.

## 7.1

Introduced the ability to disable node reuse when performing a non-streaming parse.

## 7.0

The major change introduced in version 7.0 is to use `java.io.Reader` for reading JSON strings and numbers, and `java.io.Writer` for writing JSON strings and numbers, to improve memory efficiency and to allow them to be unlimited length.  This results in breaking changes in `argo.staj.StajParser` and `argo.saj.SajParser` as methods for handling field names, string values, and number values now use `Reader` instead of `String`.  `argo.jdom.JdomParser` remains backwards-compatible with version 6.6.

Version 7.0 also begins a migration to a more intuitive package structure.  Primarily, the preferred way of creating a parser is now to instantiate `argo.JsonParser` and the preferred way of creating a generator is to instantiate `argo.JsonGenerator`.  The old package structure has been deprecated with comments explaining how to migrate.  The deprecated code will be removed in version 8.0.

In detail:

- `argo.jdom.JdomParser#parse(Reader)` now throws `IOException` propagated from the given `Reader`.
- `argo.saj.SajParser#parse(Reader)` now throws `IOException` propagated from the given `Reader`.
- `argo.saj.JsonListener` is moved to `argo.JsonListener` in preparation for removing the `argo.saj` package.
- `argo.JsonListener#startField(String)` is now passed a `Reader` rather than a `String` because the text content of nodes is now streamed so that the entire value doesn't have to be held in memory.
- `argo.JsonListener#stringValue(String)` is now passed a `Reader` rather than a `String` because the text content of nodes is now streamed so that the entire value doesn't have to be held in memory.
- `argo.JsonListener#numberValue(String)` is now passed a `Reader` rather than a `String` because the text content of nodes is now streamed so that the entire value doesn't have to be held in memory.
- `argo.staj.JsonStreamElement#text()` is replaced with `argo.staj.JsonStreamElement#reader()` because the text content of nodes is now streamed so that the entire value doesn't have to be held in memory.
- `argo.staj.JsonStreamElement` is moved to `argo.JsonStreamElement` in preparation for removing the `argo.staj` package.
- `argo.staj.JsonStreamElementType` is moved to `argo.JsonStreamElementType` in preparation for removing the `argo.staj` package.
- `argo.staj.InvalidSyntaxRuntimeException` is moved to `argo.InvalidSyntaxRuntimeException` in preparation for removing the `argo.staj` package.
- `argo.staj.JsonStreamException` is moved to `argo.JsonStreamException` in preparation for removing the `argo.staj` package.
- `argo.jdom.JsonNumberValidator` is removed; it was intended for internal use only.
- `argo.staj.InvalidSyntaxRuntimeException#asInvalidSyntaxException()` removed as it was intended for internal use only.
- `argo.staj.InvalidSyntaxRuntimeException` is now final; it was never intended for extension.
- `argo.staj.InvalidSyntaxRuntimeException` constructor is replaced with a factory method.
- `argo.jdom.JsonNode argo.jdom.JsonNodeFactories::lazyObject` is removed.  Streaming JSON generation should be done using `argo.JsonGenerator#generate(Writer, WriteableJsonObject)` or `argo.JsonGenerator#generate(WriteableJsonObject)`
- `argo.jdom.JsonNode argo.jdom.JsonNodeFactories::nullableLazyObject` is removed.  Streaming JSON generation should be done using `argo.JsonGenerator#generate(Writer, WriteableJsonObject)` or `argo.JsonGenerator#generate(WriteableJsonObject)`
- `argo.jdom.JsonNode argo.jdom.JsonNodeFactories::lazyArray` is removed.  Streaming JSON generation should be done using `argo.JsonGenerator#generate(Writer, WriteableJsonArray)` or `argo.JsonGenerator#generate(WriteableJsonArray)`
- `argo.jdom.JsonNode argo.jdom.JsonNodeFactories::nullableLazyArray` is removed.  Streaming JSON generation should be done using `argo.JsonGenerator#generate(Writer, WriteableJsonArray)` or `argo.JsonGenerator#generate(WriteableJsonArray)`
- `argo.jdom.JsonNode#visit(JsonNodeVisitor)` is added (clients of Argo are not expected to extend this class)