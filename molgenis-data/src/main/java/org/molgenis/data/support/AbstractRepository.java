package org.molgenis.data.support;

import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.partition;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Maps.uniqueIndex;
import static com.google.common.collect.Streams.stream;

import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.molgenis.data.Entity;
import org.molgenis.data.Fetch;
import org.molgenis.data.Query;
import org.molgenis.data.QueryRule.Operator;
import org.molgenis.data.Repository;
import org.molgenis.data.RepositoryCapability;
import org.molgenis.data.aggregation.AggregateQuery;
import org.molgenis.data.aggregation.AggregateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Base class for repositories. Subclasses can override supported methods */
public abstract class AbstractRepository implements Repository<Entity> {
  private static final int FIND_ALL_BATCH_SIZE = 1000;

  private final Logger log = LoggerFactory.getLogger(getClass());

  private String name;

  @Override
  public String getName() {
    if (name == null) name = getEntityType().getId();
    return name;
  }

  @Override
  public Set<Operator> getQueryOperators() {
    if (!getCapabilities().contains(RepositoryCapability.QUERYABLE)) {
      return Collections.emptySet();
    } else {
      return EnumSet.allOf(Operator.class);
    }
  }

  public Query<Entity> query() {
    return new QueryImpl<>(this);
  }

  @Override
  public long count() {
    return count(new QueryImpl<>());
  }

  @Override
  public void close() throws IOException {}

  @Override
  public long count(Query<Entity> q) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Stream<Entity> findAll(Query<Entity> q) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Entity findOne(Query<Entity> q) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Entity findOneById(Object id) {
    return findOneById(id, null);
  }

  @Override
  public Entity findOneById(Object id, Fetch fetch) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Stream<Entity> findAll(Stream<Object> ids) {
    return findAll(ids, null);
  }

  @Override
  public Stream<Entity> findAll(Stream<Object> ids, Fetch fetch) {
    Iterator<List<Object>> batches = Iterators.partition(ids.iterator(), FIND_ALL_BATCH_SIZE);
    Iterable<List<Object>> iterable = () -> batches;
    return stream(iterable).flatMap(batch -> stream(findAllBatched(batch, fetch)));
  }

  private Iterable<Entity> findAllBatched(List<Object> ids, Fetch fetch) {
    String fieldIdAttributeName = getEntityType().getIdAttribute().getName();
    if (fetch != null) fetch.field(fieldIdAttributeName);
    Query<Entity> inQuery =
        new QueryImpl<>().in(fieldIdAttributeName, Sets.newHashSet(ids)).fetch(fetch);
    Map<Object, Entity> indexedEntities =
        uniqueIndex(findAll(inQuery).iterator(), Entity::getIdValue);
    return filter(transform(ids, id -> lookup(indexedEntities, id)), notNull());
  }

  private Entity lookup(Map<Object, Entity> index, Object id) {
    Entity result = index.get(id);
    if (result == null) {
      log.debug("Lookup: Couldn't find {} for id {}.", getName(), id);
    }
    return result;
  }

  @Override
  public AggregateResult aggregate(AggregateQuery aggregateQuery) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void update(Entity entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void update(Stream<Entity> entities) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void delete(Entity entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void delete(Stream<Entity> entities) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void deleteById(Object id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void deleteAll(Stream<Object> ids) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void deleteAll() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void add(Entity entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Integer add(Stream<Entity> entities) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void forEachBatched(Fetch fetch, Consumer<List<Entity>> consumer, int batchSize) {
    // by default: ignore fetch
    for (List<Entity> entities : partition(this, batchSize)) {
      consumer.accept(entities);
    }
  }
}
