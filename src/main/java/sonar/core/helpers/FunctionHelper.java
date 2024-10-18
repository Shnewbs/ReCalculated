package sonar.core.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

public class FunctionHelper {

    public static final Function<Void, ArrayList<?>> ARRAY = (Void) -> new ArrayList<>();

    public static final Function<Void, HashMap<?, ?>> HASH_MAP = (Void) -> new HashMap<>();
}
