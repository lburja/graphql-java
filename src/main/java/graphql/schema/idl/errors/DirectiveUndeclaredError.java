package graphql.schema.idl.errors;

import graphql.Internal;
import graphql.language.Node;

import static java.lang.String.format;

@Internal
public class DirectiveUndeclaredError extends BaseError {

    public DirectiveUndeclaredError(Node element, String elementName, String directiveName) {
        super(element,
                format("'%s' %s tried to use an undeclared directive '%s'",
                        elementName, BaseError.lineCol(element), directiveName
                ));
    }
}
