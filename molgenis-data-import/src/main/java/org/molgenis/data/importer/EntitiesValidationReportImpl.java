package org.molgenis.data.importer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EntitiesValidationReportImpl implements EntitiesValidationReport {
  /** map of all sheets, and whether they are importable (recognized) or not */
  private final Map<String, Boolean> sheetsImportable;
  /** map of importable sheets and their importable fields */
  private final Map<String, Collection<String>> fieldsImportable;
  /** map of importable sheets and their unknown fields */
  private final Map<String, Collection<String>> fieldsUnknown;
  /** map of importable sheets and their required/missing fields */
  private final Map<String, Collection<String>> fieldsRequired;
  /** map of importable sheets and their available/optional fields */
  private final Map<String, Collection<String>> fieldsAvailable;
  /** import order of the sheets */
  private final List<String> importOrder;

  private final List<String> packages;

  public EntitiesValidationReportImpl() {
    this.sheetsImportable = new LinkedHashMap<>();
    this.fieldsImportable = new LinkedHashMap<>();
    this.fieldsUnknown = new LinkedHashMap<>();
    this.fieldsRequired = new LinkedHashMap<>();
    this.fieldsAvailable = new LinkedHashMap<>();
    importOrder = new ArrayList<>();
    packages = new ArrayList<>();
  }

  @Override
  public Map<String, Boolean> getSheetsImportable() {
    return sheetsImportable;
  }

  @Override
  public Map<String, Collection<String>> getFieldsImportable() {
    return fieldsImportable;
  }

  @Override
  public Map<String, Collection<String>> getFieldsUnknown() {
    return fieldsUnknown;
  }

  @Override
  public Map<String, Collection<String>> getFieldsRequired() {
    return fieldsRequired;
  }

  @Override
  public Map<String, Collection<String>> getFieldsAvailable() {
    return fieldsAvailable;
  }

  @Override
  public List<String> getImportOrder() {
    return importOrder;
  }

  @SuppressWarnings("squid:S2178") // Short-circuit logic should be used in boolean contexts
  @Override
  public boolean valid() {
    // determine if validation succeeded
    boolean ok = true;
    for (Boolean b : sheetsImportable.values()) {
      ok = ok & b;
    }

    for (Collection<String> fields : getFieldsRequired().values()) {
      ok = ok & (fields == null || fields.isEmpty());
    }
    return ok;
  }

  @Override
  public List<String> getPackages() {
    return packages;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EntitiesValidationReportImpl that = (EntitiesValidationReportImpl) o;
    return sheetsImportable.equals(that.sheetsImportable)
        && fieldsImportable.equals(that.fieldsImportable)
        && fieldsUnknown.equals(that.fieldsUnknown)
        && fieldsRequired.equals(that.fieldsRequired)
        && fieldsAvailable.equals(that.fieldsAvailable)
        && importOrder.equals(that.importOrder)
        && packages.equals(that.packages);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        sheetsImportable,
        fieldsImportable,
        fieldsUnknown,
        fieldsRequired,
        fieldsAvailable,
        importOrder,
        packages);
  }
}
