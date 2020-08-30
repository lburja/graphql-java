package graphql.language;

import graphql.Assert;
import graphql.Internal;
import graphql.execution.UnknownOperationException;
import graphql.util.FpKit;
import graphql.util.NodeLocation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static graphql.util.FpKit.mergeFirst;

/**
 * Helper class for working with {@link Node}s
 */
@Internal
public class NodeUtil {

    public static boolean isEqualTo(String thisStr, String thatStr) {
        if (null == thisStr) {
            if (null != thatStr) {
                return false;
            }
        } else if (!thisStr.equals(thatStr)) {
            return false;
        }
        return true;
    }

    public static Map<String, List<Directive>> allDirectivesByName(List<Directive> directives) {
        return FpKit.groupingBy(directives, Directive::getName);
    }

    public static Map<String, Directive> nonRepeatableDirectivesByName(List<Directive> directives) {
        Map<String, List<Directive>> map = allDirectivesByName(directives);
        List<Directive> singletonDirectives = map.entrySet().stream()
                // only those that have 1 non repeated entry
                .filter(e -> e.getKey() != null && e.getValue().size() == 1)
                .flatMap(e -> e.getValue().stream()).collect(Collectors.toList());
        return FpKit.getByName(singletonDirectives, Directive::getName);
    }

    public static Directive nonRepeatedDirectiveByNameWithAssert(Map<String, List<Directive>> directives, String directiveName) {
        List<Directive> directiveList = directives.get(directiveName);
        if (directiveList == null) {
            return null;
        }
        Assert.assertTrue(directiveList.size() == 1, () -> String.format("%s is a repeatable directive", directiveName));
        return directiveList.get(0);
    }

    public static Map<String, Argument> argumentsByName(List<Argument> arguments) {
        return FpKit.getByName(arguments, Argument::getName, mergeFirst());
    }


    public static class GetOperationResult {
        public OperationDefinition operationDefinition;
        public Map<String, FragmentDefinition> fragmentsByName;
    }

    public static Map<String, FragmentDefinition> getFragmentsByName(Document document) {
        Map<String, FragmentDefinition> fragmentsByName = new LinkedHashMap<>();

        for (Definition definition : document.getDefinitions()) {
            if (definition instanceof FragmentDefinition) {
                FragmentDefinition fragmentDefinition = (FragmentDefinition) definition;
                fragmentsByName.put(fragmentDefinition.getName(), fragmentDefinition);
            }
        }
        return fragmentsByName;
    }

    public static GetOperationResult getOperation(Document document, String operationName) {


        Map<String, FragmentDefinition> fragmentsByName = new LinkedHashMap<>();
        Map<String, OperationDefinition> operationsByName = new LinkedHashMap<>();

        for (Definition definition : document.getDefinitions()) {
            if (definition instanceof OperationDefinition) {
                OperationDefinition operationDefinition = (OperationDefinition) definition;
                operationsByName.put(operationDefinition.getName(), operationDefinition);
            }
            if (definition instanceof FragmentDefinition) {
                FragmentDefinition fragmentDefinition = (FragmentDefinition) definition;
                fragmentsByName.put(fragmentDefinition.getName(), fragmentDefinition);
            }
        }
        if (operationName == null && operationsByName.size() > 1) {
            throw new UnknownOperationException("Must provide operation name if query contains multiple operations.");
        }
        OperationDefinition operation;

        if (operationName == null || operationName.isEmpty()) {
            operation = operationsByName.values().iterator().next();
        } else {
            operation = operationsByName.get(operationName);
        }
        if (operation == null) {
            throw new UnknownOperationException(String.format("Unknown operation named '%s'.", operationName));
        }
        GetOperationResult result = new GetOperationResult();
        result.fragmentsByName = fragmentsByName;
        result.operationDefinition = operation;
        return result;
    }

    public static void assertNewChildrenAreEmpty(NodeChildrenContainer newChildren) {
        if (!newChildren.isEmpty()) {
            throw new IllegalArgumentException("Cannot pass non-empty newChildren to Node that doesn't hold children");
        }
    }

    public static Node removeChild(Node node, NodeLocation childLocationToRemove) {
        NodeChildrenContainer namedChildren = node.getNamedChildren();
        NodeChildrenContainer newChildren = namedChildren.transform(builder -> builder.removeChild(childLocationToRemove.getName(), childLocationToRemove.getIndex()));
        return node.withNewChildren(newChildren);
    }
}
