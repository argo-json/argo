## 7.0

- `argo.jdom.JdomParser#parse(Reader)` now throws `IOException` propagated from the given `Reader`.
- `argo.saj.SajParser#parse(Reader)` now throws `IOException` propagated from the given `Reader`.
- `argo.staj.JsonStreamElement#text()` is replaced with `argo.staj.JsonStreamElement#reader()` because the text content of nodes is now streamed so that the entire value doesn't have to be held in memory.
- `argo.staj.InvalidSyntaxRuntimeException#asInvalidSyntaxException()` removed as it was intended for internal use only.
- `argo.staj.InvalidSyntaxRuntimeException` is now final; it was never intended for extension.
- `argo.staj.InvalidSyntaxRuntimeException` constructor is replaced with a factory method.
- `argo.saj.JsonListener#startField(String)` is now passed a `Reader` rather than a `String` because the text content of nodes is now streamed so that the entire value doesn't have to be held in memory.
- `argo.saj.JsonListener#stringValue(String)` is now passed a `Reader` rather than a `String` because the text content of nodes is now streamed so that the entire value doesn't have to be held in memory.
- `argo.saj.JsonListener#numberValue(String)` is now passed a `Reader` rather than a `String` because the text content of nodes is now streamed so that the entire value doesn't have to be held in memory.
- `argo.jdom.JsonNumberValidator` is removed; it was intended for internal use only.
- `argo.jdom.JsonNode argo.jdom.JsonNodeFactories::lazyObject` is removed.  Streaming JSON generation should be done using `argo.JsonGenerator#generate(Writer, WriteableJsonObject)` or `argo.JsonGenerator#generate(WriteableJsonObject)`
- `argo.jdom.JsonNode argo.jdom.JsonNodeFactories::nullableLazyObject` is removed.  Streaming JSON generation should be done using `argo.JsonGenerator#generate(Writer, WriteableJsonObject)` or `argo.JsonGenerator#generate(WriteableJsonObject)`
- `argo.jdom.JsonNode argo.jdom.JsonNodeFactories::lazyArray` is removed.  Streaming JSON generation should be done using `argo.JsonGenerator#generate(Writer, WriteableJsonArray)` or `argo.JsonGenerator#generate(WriteableJsonArray)`
- `argo.jdom.JsonNode argo.jdom.JsonNodeFactories::nullableLazyArray` is removed.  Streaming JSON generation should be done using `argo.JsonGenerator#generate(Writer, WriteableJsonArray)` or `argo.JsonGenerator#generate(WriteableJsonArray)`
- `argo.jdom.JsonNode#visit(JsonNodeVisitor)` is added (clients of Argo are not expected to extend this class)