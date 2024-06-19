Contains default implememntation of the semantic layer config dto classes.
Those classes are automatically generated from the semantic layer config json schema.
The reason that this module is not a part of the semantic-layer-config module is that is that it offers a way to exclude
it and generate dto classes using a different method.
For example, asemic-cli is using micronaut framework, and it needs classes that use compile time reflection resolution.
