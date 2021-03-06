package org.molgenis.ontology.core.repository;

import static com.google.common.collect.ImmutableSet.of;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.molgenis.ontology.core.meta.OntologyTermMetadata.ID;
import static org.molgenis.ontology.core.meta.OntologyTermMetadata.ONTOLOGY;
import static org.molgenis.ontology.core.meta.OntologyTermMetadata.ONTOLOGY_TERM;
import static org.molgenis.ontology.core.meta.OntologyTermMetadata.ONTOLOGY_TERM_IRI;
import static org.molgenis.ontology.core.meta.OntologyTermMetadata.ONTOLOGY_TERM_NAME;
import static org.molgenis.ontology.core.meta.OntologyTermMetadata.ONTOLOGY_TERM_SYNONYM;
import static org.testng.Assert.assertEquals;

import java.util.List;
import java.util.stream.Stream;
import org.mockito.ArgumentCaptor;
import org.mockito.quality.Strictness;
import org.molgenis.data.AbstractMolgenisSpringTest;
import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.data.Query;
import org.molgenis.data.QueryRule;
import org.molgenis.data.support.DynamicEntity;
import org.molgenis.data.support.QueryImpl;
import org.molgenis.ontology.core.config.OntologyTestConfig;
import org.molgenis.ontology.core.meta.OntologyMetadata;
import org.molgenis.ontology.core.meta.OntologyTermMetadata;
import org.molgenis.ontology.core.meta.OntologyTermNodePathMetadata;
import org.molgenis.ontology.core.meta.OntologyTermSynonymMetadata;
import org.molgenis.ontology.core.model.OntologyTerm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

@ContextConfiguration(classes = OntologyTermRepositoryTest.Config.class)
public class OntologyTermRepositoryTest extends AbstractMolgenisSpringTest {
  @Autowired DataService dataService;

  @Autowired OntologyTermRepository ontologyTermRepository;

  @Autowired private OntologyMetadata ontologyMetadata;

  @Autowired private OntologyTermMetadata ontologyTermMetadata;

  @Autowired private OntologyTermNodePathMetadata ontologyTermNodePathMetadata;

  private org.molgenis.ontology.core.meta.OntologyTerm ontologyTermEntity;

  public OntologyTermRepositoryTest() {
    super(Strictness.WARN);
  }

  @BeforeTest
  public void beforeTest() {
    Entity ontologyEntity = mock(Entity.class);
    when(ontologyEntity.getString(OntologyMetadata.ID)).thenReturn("34");

    ontologyTermEntity = mock(org.molgenis.ontology.core.meta.OntologyTerm.class);
    when(ontologyTermEntity.getString(ID)).thenReturn("12");
    when(ontologyTermEntity.getEntity(ONTOLOGY)).thenReturn(ontologyEntity);
    when(ontologyTermEntity.getString(ONTOLOGY_TERM_IRI)).thenReturn("http://www.test.nl/iri");
    when(ontologyTermEntity.getString(ONTOLOGY_TERM_NAME)).thenReturn("Ontology term");
  }

  @Test
  public void testFindExcatOntologyTerms() {
    Entity synonymEntity1 = mock(Entity.class);
    when(synonymEntity1.get(OntologyTermSynonymMetadata.ID)).thenReturn("synonym-1");
    when(synonymEntity1.get(OntologyTermSynonymMetadata.ONTOLOGY_TERM_SYNONYM_ATTR))
        .thenReturn("Weight Reduction Diet");

    Entity synonymEntity2 = mock(Entity.class);
    when(synonymEntity2.get(OntologyTermSynonymMetadata.ID)).thenReturn("synonym-2");
    when(synonymEntity2.get(OntologyTermSynonymMetadata.ONTOLOGY_TERM_SYNONYM_ATTR))
        .thenReturn("Weight loss Diet");

    Entity synonymEntity3 = mock(Entity.class);
    when(synonymEntity3.get(OntologyTermSynonymMetadata.ID)).thenReturn("synonym-3");
    when(synonymEntity3.get(OntologyTermSynonymMetadata.ONTOLOGY_TERM_SYNONYM_ATTR))
        .thenReturn("Diet, Reducing");

    Entity ontologyTermEntity1 = mock(Entity.class);
    when(ontologyTermEntity1.getString(ID)).thenReturn("1");
    when(ontologyTermEntity1.get(ONTOLOGY)).thenReturn("34");
    when(ontologyTermEntity1.getString(ONTOLOGY_TERM_IRI)).thenReturn("http://www.test.nl/iri/1");
    when(ontologyTermEntity1.getString(ONTOLOGY_TERM_NAME)).thenReturn("Diet, Reducing");
    when(ontologyTermEntity1.get(ONTOLOGY_TERM_SYNONYM))
        .thenReturn(asList(synonymEntity1, synonymEntity2, synonymEntity3));

    Entity synonymEntity4 = mock(Entity.class);
    when(synonymEntity4.get(OntologyTermSynonymMetadata.ID)).thenReturn("synonym-4");
    when(synonymEntity4.get(OntologyTermSynonymMetadata.ONTOLOGY_TERM_SYNONYM_ATTR))
        .thenReturn("Weight");

    Entity ontologyTermEntity2 = mock(Entity.class);
    when(ontologyTermEntity2.getString(ID)).thenReturn("12");
    when(ontologyTermEntity2.get(ONTOLOGY)).thenReturn("34");
    when(ontologyTermEntity2.getString(ONTOLOGY_TERM_IRI)).thenReturn("http://www.test.nl/iri/2");
    when(ontologyTermEntity2.getString(ONTOLOGY_TERM_NAME)).thenReturn("Weight");
    when(ontologyTermEntity2.get(ONTOLOGY_TERM_SYNONYM)).thenReturn(singletonList(synonymEntity4));

    @SuppressWarnings("unchecked")
    ArgumentCaptor<Query<Entity>> queryCaptor = forClass(Query.class);
    when(dataService.findAll(eq(ONTOLOGY_TERM), queryCaptor.capture()))
        .thenReturn(Stream.of(ontologyTermEntity1, ontologyTermEntity2));

    List<OntologyTerm> exactOntologyTerms =
        ontologyTermRepository.findExcatOntologyTerms(asList("1", "2"), of("weight"), 100);

    assertEquals(
        exactOntologyTerms,
        singletonList(
            OntologyTerm.create(
                "http://www.test.nl/iri/2", "Weight", null, singletonList("Weight"))));
  }

  @Test
  public void testFindOntologyTerms() {
    @SuppressWarnings("unchecked")
    ArgumentCaptor<Query<Entity>> queryCaptor = forClass(Query.class);
    when(dataService.findAll(eq(ONTOLOGY_TERM), queryCaptor.capture()))
        .thenReturn(Stream.of(ontologyTermEntity));

    List<OntologyTerm> terms =
        ontologyTermRepository.findOntologyTerms(
            asList("1", "2"), of("term1", "term2", "term3"), 100);

    assertEquals(
        terms,
        singletonList(
            OntologyTerm.create(
                "http://www.test.nl/iri", "Ontology term", null, singletonList("Ontology term"))));
    assertEquals(
        queryCaptor.getValue().toString(),
        "rules=['ontology' IN [1, 2], AND, ('ontologyTermSynonym' FUZZY_MATCH 'term1', OR, 'ontologyTermSynonym' FUZZY_MATCH 'term2', OR, 'ontologyTermSynonym' FUZZY_MATCH 'term3')], pageSize=100");
  }

  @Test
  public void testGetChildOntologyTermsByNodePath() {
    Entity ontologyEntity = new DynamicEntity(ontologyMetadata);
    ontologyEntity.set(OntologyMetadata.ONTOLOGY_IRI, "http://www.molgenis.org");
    ontologyEntity.set(OntologyMetadata.ONTOLOGY_NAME, "molgenis");

    Entity nodePathEntity_1 = new DynamicEntity(ontologyTermNodePathMetadata);
    nodePathEntity_1.set(OntologyTermNodePathMetadata.NODE_PATH, "0[0].1[1]");
    Entity nodePathEntity_2 = new DynamicEntity(ontologyTermNodePathMetadata);
    nodePathEntity_2.set(OntologyTermNodePathMetadata.NODE_PATH, "0[0].1[1].0[2]");
    Entity nodePathEntity_3 = new DynamicEntity(ontologyTermNodePathMetadata);
    nodePathEntity_3.set(OntologyTermNodePathMetadata.NODE_PATH, "0[0].1[1].1[2]");

    Entity ontologyTerm_2 = new DynamicEntity(ontologyTermMetadata);
    ontologyTerm_2.set(ONTOLOGY, ontologyEntity);
    ontologyTerm_2.set(ONTOLOGY_TERM_IRI, "iri 2");
    ontologyTerm_2.set(ONTOLOGY_TERM_NAME, "name 2");
    ontologyTerm_2.set(
        OntologyTermMetadata.ONTOLOGY_TERM_NODE_PATH, asList(nodePathEntity_1, nodePathEntity_2));
    ontologyTerm_2.set(ONTOLOGY_TERM_SYNONYM, emptyList());

    Entity ontologyTerm_3 = new DynamicEntity(ontologyTermMetadata);
    ontologyTerm_3.set(ONTOLOGY, ontologyEntity);
    ontologyTerm_3.set(ONTOLOGY_TERM_IRI, "iri 3");
    ontologyTerm_3.set(ONTOLOGY_TERM_NAME, "name 3");
    ontologyTerm_3.set(
        OntologyTermMetadata.ONTOLOGY_TERM_NODE_PATH, singletonList(nodePathEntity_3));
    ontologyTerm_3.set(ONTOLOGY_TERM_SYNONYM, emptyList());

    when(dataService.findAll(
            ONTOLOGY_TERM,
            new QueryImpl<>(
                    new QueryRule(
                        OntologyTermMetadata.ONTOLOGY_TERM_NODE_PATH,
                        QueryRule.Operator.FUZZY_MATCH,
                        "\"0[0].1[1]\""))
                .and()
                .eq(ONTOLOGY, ontologyEntity)))
        .thenReturn(Stream.of(ontologyTerm_2, ontologyTerm_3));

    List<OntologyTerm> childOntologyTermsByNodePath =
        ontologyTermRepository.getChildOntologyTermsByNodePath(ontologyEntity, nodePathEntity_1);

    assertEquals(childOntologyTermsByNodePath.size(), 2);
    assertEquals(
        childOntologyTermsByNodePath.get(0),
        OntologyTerm.create("iri 2", "name 2", null, singletonList("name 2")));
    assertEquals(
        childOntologyTermsByNodePath.get(1),
        OntologyTerm.create("iri 3", "name 3", null, singletonList("name 3")));
  }

  @Test
  public void testCalculateNodePathDistance() {
    // Case 1
    assertEquals(
        ontologyTermRepository.calculateNodePathDistance("0[0].0[1]", "0[0].0[1].1[2]"), 1);

    // Case 2
    assertEquals(
        ontologyTermRepository.calculateNodePathDistance("0[0].0[1].1[2]", "0[0].0[1]"), 1);

    // Case 3
    assertEquals(
        ontologyTermRepository.calculateNodePathDistance(
            "0[0].0[1].1[2].2[3]", "0[0].0[1].0[2].2[3]"),
        4);

    // Case 4
    assertEquals(
        ontologyTermRepository.calculateNodePathDistance("0[0].0[1]", "0[0].0[1].0[2].1[3].2[4]"),
        3);

    // Case 5
    assertEquals(ontologyTermRepository.calculateNodePathDistance("0[0].0[1]", "0[0].0[1]"), 0);
  }

  @Test
  public void testGetOntologyTerm() {
    @SuppressWarnings("unchecked")
    Query<org.molgenis.ontology.core.meta.OntologyTerm> query = mock(Query.class, RETURNS_SELF);
    when(dataService.query(ONTOLOGY_TERM, org.molgenis.ontology.core.meta.OntologyTerm.class))
        .thenReturn(query);
    when(query.eq(ONTOLOGY_TERM_IRI, "http://www.test.nl/iri").findOne())
        .thenReturn(ontologyTermEntity);

    String[] iris = {"http://www.test.nl/iri"};

    OntologyTerm ontologyTerm = ontologyTermRepository.getOntologyTerm(iris);
    assertEquals(
        ontologyTerm,
        OntologyTerm.create(
            "http://www.test.nl/iri", "Ontology term", singletonList("Ontology term")));
  }

  @Configuration
  @Import(OntologyTestConfig.class)
  public static class Config {
    @Autowired private DataService dataService;

    @Bean
    public DataService dataService() {
      return mock(DataService.class);
    }

    @Bean
    public OntologyTermRepository ontologyTermRepository() {
      return new OntologyTermRepository(dataService);
    }
  }
}
