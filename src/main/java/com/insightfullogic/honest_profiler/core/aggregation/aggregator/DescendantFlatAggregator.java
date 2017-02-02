package com.insightfullogic.honest_profiler.core.aggregation.aggregator;

import static java.util.stream.Collector.of;
import static java.util.stream.Collectors.groupingBy;

import java.util.ArrayList;
import java.util.List;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;

/**
 * Aggregator which takes a {@link Node}, and aggregates the values of the {@link Node} and its descendants into a
 * {@link Flat}.
 */
public class DescendantFlatAggregator implements SubAggregator<Node, Entry>
{
    /**
     * This method aggregates a {@link Node} and all its all descendants.
     *
     * @see SubAggregator#aggregate(Object, LeanNode)
     */
    @Override
    public Flat aggregate(AggregationProfile source, Node parent)
    {
        List<Entry> result = new ArrayList<>();
        Flat aggregation = new Flat(source, result);

        result.addAll(
            parent.flatten().collect(
                groupingBy(
                    Node::getKey,
                    of(
                        // Supplier
                        () ->
                        {
                            Entry entry = new Entry(aggregation);
                            entry.setReference(source.getGlobalData());
                            return entry;
                        },
                        // Accumulator
                        (x, y) -> x.combine(y),
                        // Combiner
                        (x, y) -> x.combine(y)
                    )
                )
            ).values());

        return aggregation;
    }
}