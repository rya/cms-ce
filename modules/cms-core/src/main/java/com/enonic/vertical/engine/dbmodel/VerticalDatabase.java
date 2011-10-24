/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Database;

public final class VerticalDatabase extends Database {
	private static final VerticalDatabase Vertical = new VerticalDatabase("Vertical",201);

	public ModelVersionTable tModelVersion = ModelVersionTable.getInstance();
	public BinaryDataTable tBinaryData = BinaryDataTable.getInstance();
	public BlobStoreTable tBlobStore = BlobStoreTable.getInstance();
	public CatAccessRightTable tCatAccessRight = CatAccessRightTable.getInstance();
	public CategoryTable tCategory = CategoryTable.getInstance();
	public ConAccessRight2Table tConAccessRight2 = ConAccessRight2Table.getInstance();
	public ContentTable tContent = ContentTable.getInstance();
	public ContentBinaryDataTable tContentBinaryData = ContentBinaryDataTable.getInstance();
	public ContentHandlerTable tContentHandler = ContentHandlerTable.getInstance();
	public ContentHomeTable tContentHome = ContentHomeTable.getInstance();
	public ContentIndexTable tContentIndex = ContentIndexTable.getInstance();
	public ContentObjectTable tContentObject = ContentObjectTable.getInstance();
	public ContentTypeTable tContentType = ContentTypeTable.getInstance();
	public ContentVersionTable tContentVersion = ContentVersionTable.getInstance();
	public DefaultMenuARTable tDefaultMenuAR = DefaultMenuARTable.getInstance();
	public DomainTable tDomain = DomainTable.getInstance();
	public GroupTable tGroup = GroupTable.getInstance();
	public GrpGrpMembershipTable tGrpGrpMembership = GrpGrpMembershipTable.getInstance();
	public KeyTable tKey = KeyTable.getInstance();
	public LanguageTable tLanguage = LanguageTable.getInstance();
	public LogEntryTable tLogEntry = LogEntryTable.INSTANCE;
	public MenuTable tMenu = MenuTable.INSTANCE;
	public MenuItemTable tMenuItem = MenuItemTable.getInstance();
	public MenuItemARTable tMenuItemAR = MenuItemARTable.getInstance();
	public MenuItemContentTable tMenuItemContent = MenuItemContentTable.getInstance();
	public PageTable tPage = PageTable.getInstance();
	public PageConObjTable tPageConObj = PageConObjTable.getInstance();
	public PageTemplateTable tPageTemplate = PageTemplateTable.getInstance();
	public PageTemplateCtyTable tPageTemplateCty = PageTemplateCtyTable.getInstance();
	public PageTemplConObjTable tPageTemplConObj = PageTemplConObjTable.getInstance();
	public PageTemplParamTable tPageTemplParam = PageTemplParamTable.getInstance();
	public RelatedContentTable tRelatedContent = RelatedContentTable.getInstance();
	public SecConTypeFilter2Table tSecConTypeFilter2 = SecConTypeFilter2Table.getInstance();
	public SectionContent2Table tSectionContent2 = SectionContent2Table.getInstance();
	public UnitTable tUnit = UnitTable.getInstance();
	public UnitContentTypeTable tUnitContentType = UnitContentTypeTable.getInstance();
	public UserTable tUser = UserTable.INSTANCE;
	public UserFieldTable tUserField = UserFieldTable.getInstance();
	public UserMenuGUIDTable tUserMenuGUID = UserMenuGUIDTable.getInstance();
	public PreferencesTable tPreferences = PreferencesTable.getInstance();
	public VirtualFileTable tVirtualFile = VirtualFileTable.getInstance();

	private VerticalDatabase(String databaseName, int version) {
		super(databaseName, version);
		addTable(tModelVersion);
		addTable(tBinaryData);
		addTable(tBlobStore);
		addTable(tCatAccessRight);
		addTable(tCategory);
		addTable(tConAccessRight2);
		addTable(tContent);
		addTable(tContentBinaryData);
		addTable(tContentHandler);
		addTable(tContentHome);
		addTable(tContentIndex);
		addTable(tContentObject);
		addTable(tContentType);
		addTable(tContentVersion);
		addTable(tDefaultMenuAR);
		addTable(tDomain);
		addTable(tGroup);
		addTable(tGrpGrpMembership);
		addTable(tKey);
		addTable(tLanguage);
		addTable(tLogEntry);
		addTable(tMenu);
		addTable(tMenuItem);
		addTable(tMenuItemAR);
		addTable(tMenuItemContent);
		addTable(tPage);
		addTable(tPageConObj);
		addTable(tPageTemplate);
		addTable(tPageTemplateCty);
		addTable(tPageTemplConObj);
		addTable(tPageTemplParam);
		addTable(tRelatedContent);
		addTable(tSecConTypeFilter2);
		addTable(tSectionContent2);
		addTable(tUnit);
		addTable(tUnitContentType);
		addTable(tUser);
		addTable(tUserField);
		addTable(tUserMenuGUID);
		addTable(tPreferences);
		addTable(tVirtualFile);
		setDatabaseMappings();
	}

	public static VerticalDatabase getInstance() {
		return Vertical;
	}

}
