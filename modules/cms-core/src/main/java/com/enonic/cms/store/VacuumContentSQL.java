/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store;

/**
 * Nov 24, 2010
 */
public class VacuumContentSQL
{

    /**
     * Binary reference sql.
     */
    private final static String VACUUM_CONTENT_BINARY_DATA_SQL =
        "DELETE FROM tContentBinaryData WHERE cbd_cov_lKey IN (" + "SELECT cov_lKey FROM tContentVersion WHERE cov_con_lKey IN (" +
            "SELECT con_lKey FROM tContent WHERE con_bdeleted = 1))";

    /**
     * Delete menu item content.
     */
    private final static String VACUUM_MENU_ITEM_CONTENT_SQL =
        "DELETE FROM tMenuItemContent WHERE mic_con_lKey IN (" + "SELECT con_lKey FROM tContent WHERE con_bDeleted = 1)";

    /**
     * Delete menu item content.
     */
    private final static String VACUUM_SECTION_CONTENT_SQL =
        "DELETE FROM tSectionContent2 WHERE sco_con_lKey IN (" + "SELECT con_lKey FROM tContent WHERE con_bDeleted = 1)";

    /**
     * Delete binary data sql.
     */
    private final static String VCACUUM_BINARY_DATA_SQL =
        "DELETE FROM tBinaryData WHERE bda_lKey NOT IN (" + "SELECT cbd_bda_lKey FROM tContentBinaryData)";

    /**
     * Delete related content.
     */
    private final static String VACUUM_RELATED_CONTENT_SQL =
        "DELETE FROM tRelatedContent WHERE rco_con_lParent IN (" + "SELECT cov_lKey FROM tContentVersion WHERE cov_con_lKey IN (" +
            "SELECT con_lKey FROM tcontent WHERE con_bdeleted = 1))";

    /**
     * Delete content version.
     */
    private final static String VACUUM_CONTENT_VERSION_SQL =
        "DELETE FROM tContentVersion WHERE cov_con_lKey IN (" + "SELECT con_lKey FROM tcontent WHERE con_bdeleted = 1)";

    /**
     * Delete content access rights.
     */
    private final static String VACUUM_CONTENT_ACCESS_RIGHT_SQL =
        "DELETE FROM tConAccessRight2 WHERE coa_con_lKey IN (" + "SELECT con_lkey FROM tContent WHERE con_bdeleted = 1)";

    /**
     * Delete child releated contents.
     */
    private final static String VACUUM_CHILD_RELATED_CONTENT_SQL =
        "DELETE FROM tRelatedContent WHERE rco_con_lChild IN (" + "SELECT con_lkey FROM tContent WHERE con_bdeleted = 1)";

    /**
     * Delete content home.
     */
    private final static String VACUUM_CONTENT_HOME_SQL =
        "DELETE FROM tContentHome WHERE cho_con_lKey IN (" + "SELECT con_lKey FROM tContent WHERE con_bDeleted = 1)";

    /**
     * Delete content.
     */
    private final static String VACUUM_CONTENT_SQL = "DELETE FROM tContent WHERE con_bdeleted = 1";

    private final static String DELETED_CATEGORIES_WITHOUT_CONTENT_AND_SUB_CATEGORIES =
        "SELECT cat_lkey FROM tCategory WHERE cat_bdeleted = 1 AND NOT EXISTS ( select con_lkey from tContent where con_cat_lkey = cat_lkey )" +
            "AND NOT EXISTS ( select cat_lkey from tCategory where cat_cat_lsuper = cat_lkey )";

    private final static String DELETED_UNITS_WITHOUT_CATEGORIES =
        "SELECT uni_lkey FROM tUnit WHERE uni_bdeleted = 1 AND uni_lkey NOT IN ( SELECT cat_uni_lkey FROM tCategory WHERE cat_lkey IN ( " +
            DELETED_CATEGORIES_WITHOUT_CONTENT_AND_SUB_CATEGORIES + " ) )";

    private final static String VACUUM_CATEGORY_ACCESSRIGHTS_WITHOUT_CONTENT_SQL =
        "DELETE FROM tCatAccessright WHERE car_cat_lkey IN ( " + DELETED_CATEGORIES_WITHOUT_CONTENT_AND_SUB_CATEGORIES + " )";

    private final static String VACUUM_CATEGORIES_WITHOUT_CONTENT_SQL =
        "DELETE FROM tCategory WHERE cat_lkey IN (" + DELETED_CATEGORIES_WITHOUT_CONTENT_AND_SUB_CATEGORIES + ")";

    private final static String VACUUM_UNITCONTENTTYPES_BELONGING_TO_UNITS_WITHOUT_CATEGORIES_SQL =
        "DELETE FROM tUnitContentType WHERE uct_uni_lkey IN (" + DELETED_UNITS_WITHOUT_CATEGORIES + ")";

    private final static String VACUUM_UNITS_WITHOUT_CATEGORIES_SQL =
        "DELETE FROM tUnit WHERE uni_lkey IN (" + DELETED_UNITS_WITHOUT_CATEGORIES + ")";

    public final static String[] VACUUM_BINARIES_STATEMENTS = new String[]{VACUUM_CONTENT_BINARY_DATA_SQL, VCACUUM_BINARY_DATA_SQL};

    public final static String[] VACUUM_CONTENT_STATEMENTS =
        new String[]{VACUUM_RELATED_CONTENT_SQL, VACUUM_CONTENT_VERSION_SQL, VACUUM_CONTENT_ACCESS_RIGHT_SQL,
            VACUUM_CHILD_RELATED_CONTENT_SQL, VACUUM_CONTENT_HOME_SQL, VACUUM_SECTION_CONTENT_SQL, VACUUM_MENU_ITEM_CONTENT_SQL,
            VACUUM_CONTENT_SQL};

    public final static String[] VACUUM_CATEGORIES_STATEMENTS =
        new String[]{VACUUM_CATEGORY_ACCESSRIGHTS_WITHOUT_CONTENT_SQL, VACUUM_CATEGORIES_WITHOUT_CONTENT_SQL};

    public final static String[] VACUUM_ARCHIVES_STATEMENTS =
        new String[]{VACUUM_UNITCONTENTTYPES_BELONGING_TO_UNITS_WITHOUT_CATEGORIES_SQL, VACUUM_UNITS_WITHOUT_CATEGORIES_SQL};
}
