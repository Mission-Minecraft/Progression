package joshie.progression.api.criteria;

import java.util.List;

public interface IProgressionFilter extends IFieldProvider {
    /** Return true if the pass in object, matches this filter.
     *  Keep in mind this can pass in entities, itemstack,
     *  Lists or anything really, so make sure to validate
     *  all objects */
    public boolean matches(Object object);
    
    /** Returns a list of all things that match this filter
     *  May pass in something, or it can be null **/
    public List getMatches(Object object);
    
    /** Returns the type of filter this is **/
    public IProgressionFilterSelector getType();
}
