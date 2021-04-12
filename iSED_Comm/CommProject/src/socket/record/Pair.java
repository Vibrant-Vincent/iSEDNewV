package socket.record;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Pair class
public class  Pair<U, V>
{
    public final U key;       // first field of a Pair
    public final V value;      // second field of a Pair

    // Constructs a new Pair with specified values
    public Pair(U first, V second)
    {
        this.key = first;
        this.value = second;
    }

    @Override
    // Checks specified object is "equal to" current object or not
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Pair<?, ?> pair = (Pair<?, ?>) o;

        // call equals() method of the underlying objects
        if (!key.equals(pair.key))
            return false;
        return value.equals(pair.value);
    }

    @Override
    // Computes hash code for an object to support hash tables
    public int hashCode()
    {
        // use hash codes of the underlying objects
        return 31 * key.hashCode() + value.hashCode();
    }

    @Override
    public String toString()
    {
        return "(" + key + ", " + value + ")";
    }

    // Factory method for creating a Typed Pair immutable instance
    public static <U, V> Pair <U, V> of(U a, V b)
    {
        // calls private constructor
        return new Pair<>(a, b);
    }
}
