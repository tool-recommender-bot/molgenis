package org.molgenis.ontology.matching;

import org.molgenis.MolgenisFieldTypes.FieldTypeEnum;
import org.molgenis.data.EntityMetaData;
import org.molgenis.data.support.DefaultAttributeMetaData;
import org.molgenis.data.support.DefaultEntityMetaData;

public class MathcingTaskContentEntity
{
	public final static String ENTITY_NAME = "MatchingTaskContent";
	public final static String IDENTIFIER = "Identifier";
	public final static String INPUT_TERM = "Input_term";
	public final static String MATCHED_TERM = "Match_term";
	public final static String SCORE = "Score";
	public final static String VALIDATED = "Validated";

	public static EntityMetaData getEntityMetaData()
	{
		DefaultEntityMetaData defaultEntityMetaData = new DefaultEntityMetaData(ENTITY_NAME);
		DefaultAttributeMetaData identifierAttr = new DefaultAttributeMetaData(IDENTIFIER);
		identifierAttr.setIdAttribute(true);
		identifierAttr.setNillable(false);
		defaultEntityMetaData.addAttributeMetaData(identifierAttr);

		DefaultAttributeMetaData inputTermAttr = new DefaultAttributeMetaData(INPUT_TERM);
		inputTermAttr.setNillable(false);
		defaultEntityMetaData.addAttributeMetaData(inputTermAttr);

		DefaultAttributeMetaData matchedTermAttr = new DefaultAttributeMetaData(MATCHED_TERM);
		matchedTermAttr.setNillable(false);
		defaultEntityMetaData.addAttributeMetaData(matchedTermAttr);

		DefaultAttributeMetaData scoreAttr = new DefaultAttributeMetaData(SCORE, FieldTypeEnum.DECIMAL);
		scoreAttr.setNillable(false);
		defaultEntityMetaData.addAttributeMetaData(scoreAttr);
		DefaultAttributeMetaData validatedAttr = new DefaultAttributeMetaData(VALIDATED, FieldTypeEnum.BOOL);
		validatedAttr.setNillable(false);
		defaultEntityMetaData.addAttributeMetaData(validatedAttr);
		return defaultEntityMetaData;
	}
}
