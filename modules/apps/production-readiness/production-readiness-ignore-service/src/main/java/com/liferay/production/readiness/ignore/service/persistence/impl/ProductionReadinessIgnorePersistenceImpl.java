/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.ignore.service.persistence.impl;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.production.readiness.ignore.exception.NoSuchProductionReadinessIgnoreException;
import com.liferay.production.readiness.ignore.model.ProductionReadinessIgnore;
import com.liferay.production.readiness.ignore.model.ProductionReadinessIgnoreTable;
import com.liferay.production.readiness.ignore.model.impl.ProductionReadinessIgnoreImpl;
import com.liferay.production.readiness.ignore.model.impl.ProductionReadinessIgnoreModelImpl;
import com.liferay.production.readiness.ignore.service.persistence.ProductionReadinessIgnorePersistence;
import com.liferay.production.readiness.ignore.service.persistence.ProductionReadinessIgnoreUtil;
import com.liferay.production.readiness.ignore.service.persistence.impl.constants.PRPersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the production readiness ignore service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = ProductionReadinessIgnorePersistence.class)
public class ProductionReadinessIgnorePersistenceImpl
	extends BasePersistenceImpl
		<ProductionReadinessIgnore, NoSuchProductionReadinessIgnoreException>
	implements ProductionReadinessIgnorePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ProductionReadinessIgnoreUtil</code> to access the production readiness ignore persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ProductionReadinessIgnoreImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;
	private CollectionPersistenceFinder<ProductionReadinessIgnore>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns all the production readiness ignores where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching production readiness ignores
	 */
	@Override
	public List<ProductionReadinessIgnore> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the production readiness ignores where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ProductionReadinessIgnoreModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of production readiness ignores
	 * @param end the upper bound of the range of production readiness ignores (not inclusive)
	 * @return the range of matching production readiness ignores
	 */
	@Override
	public List<ProductionReadinessIgnore> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the production readiness ignores where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ProductionReadinessIgnoreModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of production readiness ignores
	 * @param end the upper bound of the range of production readiness ignores (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching production readiness ignores
	 */
	@Override
	public List<ProductionReadinessIgnore> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<ProductionReadinessIgnore> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the production readiness ignores where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ProductionReadinessIgnoreModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of production readiness ignores
	 * @param end the upper bound of the range of production readiness ignores (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching production readiness ignores
	 */
	@Override
	public List<ProductionReadinessIgnore> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<ProductionReadinessIgnore> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first production readiness ignore in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching production readiness ignore
	 * @throws NoSuchProductionReadinessIgnoreException if a matching production readiness ignore could not be found
	 */
	@Override
	public ProductionReadinessIgnore findByCompanyId_First(
			long companyId,
			OrderByComparator<ProductionReadinessIgnore> orderByComparator)
		throws NoSuchProductionReadinessIgnoreException {

		ProductionReadinessIgnore productionReadinessIgnore =
			fetchByCompanyId_First(companyId, orderByComparator);

		if (productionReadinessIgnore != null) {
			return productionReadinessIgnore;
		}

		throw new NoSuchProductionReadinessIgnoreException(
			_collectionPersistenceFinderByCompanyId.buildNoSuchKeyMessage(
				_NO_SUCH_ENTITY_WITH_KEY, new Object[] {companyId}));
	}

	/**
	 * Returns the first production readiness ignore in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching production readiness ignore, or <code>null</code> if a matching production readiness ignore could not be found
	 */
	@Override
	public ProductionReadinessIgnore fetchByCompanyId_First(
		long companyId,
		OrderByComparator<ProductionReadinessIgnore> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the production readiness ignores where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of production readiness ignores where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching production readiness ignores
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private FinderPath _finderPathFetchByC_R;
	private UniquePersistenceFinder<ProductionReadinessIgnore>
		_uniquePersistenceFinderByC_R;

	/**
	 * Returns the production readiness ignore where companyId = &#63; and ruleKey = &#63; or throws a <code>NoSuchProductionReadinessIgnoreException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @return the matching production readiness ignore
	 * @throws NoSuchProductionReadinessIgnoreException if a matching production readiness ignore could not be found
	 */
	@Override
	public ProductionReadinessIgnore findByC_R(long companyId, String ruleKey)
		throws NoSuchProductionReadinessIgnoreException {

		ProductionReadinessIgnore productionReadinessIgnore = fetchByC_R(
			companyId, ruleKey);

		if (productionReadinessIgnore == null) {
			String message =
				_uniquePersistenceFinderByC_R.buildNoSuchKeyMessage(
					_NO_SUCH_ENTITY_WITH_KEY,
					new Object[] {companyId, ruleKey});

			if (_log.isDebugEnabled()) {
				_log.debug(message);
			}

			throw new NoSuchProductionReadinessIgnoreException(message);
		}

		return productionReadinessIgnore;
	}

	/**
	 * Returns the production readiness ignore where companyId = &#63; and ruleKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @return the matching production readiness ignore, or <code>null</code> if a matching production readiness ignore could not be found
	 */
	@Override
	public ProductionReadinessIgnore fetchByC_R(
		long companyId, String ruleKey) {

		return fetchByC_R(companyId, ruleKey, true);
	}

	/**
	 * Returns the production readiness ignore where companyId = &#63; and ruleKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching production readiness ignore, or <code>null</code> if a matching production readiness ignore could not be found
	 */
	@Override
	public ProductionReadinessIgnore fetchByC_R(
		long companyId, String ruleKey, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_R.fetch(
			finderCache, new Object[] {companyId, ruleKey}, useFinderCache);
	}

	/**
	 * Removes the production readiness ignore where companyId = &#63; and ruleKey = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @return the production readiness ignore that was removed
	 */
	@Override
	public ProductionReadinessIgnore removeByC_R(long companyId, String ruleKey)
		throws NoSuchProductionReadinessIgnoreException {

		ProductionReadinessIgnore productionReadinessIgnore = findByC_R(
			companyId, ruleKey);

		return remove(productionReadinessIgnore);
	}

	/**
	 * Returns the number of production readiness ignores where companyId = &#63; and ruleKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @return the number of matching production readiness ignores
	 */
	@Override
	public int countByC_R(long companyId, String ruleKey) {
		return _uniquePersistenceFinderByC_R.count(
			finderCache, new Object[] {companyId, ruleKey});
	}

	public ProductionReadinessIgnorePersistenceImpl() {
		setModelClass(ProductionReadinessIgnore.class);

		setModelImplClass(ProductionReadinessIgnoreImpl.class);
		setModelPKClass(long.class);

		setTable(ProductionReadinessIgnoreTable.INSTANCE);
	}

	/**
	 * Creates a new production readiness ignore with the primary key. Does not add the production readiness ignore to the database.
	 *
	 * @param productionReadinessIgnoreId the primary key for the new production readiness ignore
	 * @return the new production readiness ignore
	 */
	@Override
	public ProductionReadinessIgnore create(long productionReadinessIgnoreId) {
		ProductionReadinessIgnore productionReadinessIgnore =
			new ProductionReadinessIgnoreImpl();

		productionReadinessIgnore.setNew(true);
		productionReadinessIgnore.setPrimaryKey(productionReadinessIgnoreId);

		productionReadinessIgnore.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return productionReadinessIgnore;
	}

	/**
	 * Removes the production readiness ignore with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param productionReadinessIgnoreId the primary key of the production readiness ignore
	 * @return the production readiness ignore that was removed
	 * @throws NoSuchProductionReadinessIgnoreException if a production readiness ignore with the primary key could not be found
	 */
	@Override
	public ProductionReadinessIgnore remove(long productionReadinessIgnoreId)
		throws NoSuchProductionReadinessIgnoreException {

		return remove((Serializable)productionReadinessIgnoreId);
	}

	@Override
	protected ProductionReadinessIgnore removeImpl(
		ProductionReadinessIgnore productionReadinessIgnore) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(productionReadinessIgnore)) {
				productionReadinessIgnore =
					(ProductionReadinessIgnore)session.get(
						ProductionReadinessIgnoreImpl.class,
						productionReadinessIgnore.getPrimaryKeyObj());
			}

			if (productionReadinessIgnore != null) {
				session.delete(productionReadinessIgnore);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (productionReadinessIgnore != null) {
			clearCache(productionReadinessIgnore);
		}

		return productionReadinessIgnore;
	}

	@Override
	public ProductionReadinessIgnore updateImpl(
		ProductionReadinessIgnore productionReadinessIgnore) {

		boolean isNew = productionReadinessIgnore.isNew();

		if (!(productionReadinessIgnore instanceof
				ProductionReadinessIgnoreModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(productionReadinessIgnore.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					productionReadinessIgnore);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in productionReadinessIgnore proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ProductionReadinessIgnore implementation " +
					productionReadinessIgnore.getClass());
		}

		ProductionReadinessIgnoreModelImpl productionReadinessIgnoreModelImpl =
			(ProductionReadinessIgnoreModelImpl)productionReadinessIgnore;

		if (isNew && (productionReadinessIgnore.getCreateDate() == null)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				productionReadinessIgnore.setCreateDate(date);
			}
			else {
				productionReadinessIgnore.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(productionReadinessIgnore);
			}
			else {
				productionReadinessIgnore =
					(ProductionReadinessIgnore)session.merge(
						productionReadinessIgnore);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(productionReadinessIgnore, false);

		if (isNew) {
			productionReadinessIgnore.setNew(false);
		}

		productionReadinessIgnore.resetOriginalValues();

		return productionReadinessIgnore;
	}

	/**
	 * Returns the production readiness ignore with the primary key or throws a <code>NoSuchProductionReadinessIgnoreException</code> if it could not be found.
	 *
	 * @param productionReadinessIgnoreId the primary key of the production readiness ignore
	 * @return the production readiness ignore
	 * @throws NoSuchProductionReadinessIgnoreException if a production readiness ignore with the primary key could not be found
	 */
	@Override
	public ProductionReadinessIgnore findByPrimaryKey(
			long productionReadinessIgnoreId)
		throws NoSuchProductionReadinessIgnoreException {

		return findByPrimaryKey((Serializable)productionReadinessIgnoreId);
	}

	/**
	 * Returns the production readiness ignore with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param productionReadinessIgnoreId the primary key of the production readiness ignore
	 * @return the production readiness ignore, or <code>null</code> if a production readiness ignore with the primary key could not be found
	 */
	@Override
	public ProductionReadinessIgnore fetchByPrimaryKey(
		long productionReadinessIgnoreId) {

		return fetchByPrimaryKey((Serializable)productionReadinessIgnoreId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "productionReadinessIgnoreId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PRODUCTIONREADINESSIGNORE;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ProductionReadinessIgnoreModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the production readiness ignore persistence.
	 */
	@Activate
	public void activate() {
		_finderPathWithPaginationFindByCompanyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCompanyId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"companyId"}, true);

		_finderPathWithoutPaginationFindByCompanyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCompanyId",
			new String[] {Long.class.getName()}, new String[] {"companyId"},
			true);

		_finderPathCountByCompanyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCompanyId",
			new String[] {Long.class.getName()}, new String[] {"companyId"},
			false);

		_collectionPersistenceFinderByCompanyId =
			new CollectionPersistenceFinder<>(
				this, _finderPathWithPaginationFindByCompanyId,
				_finderPathWithoutPaginationFindByCompanyId,
				_finderPathCountByCompanyId,
				_SQL_SELECT_PRODUCTIONREADINESSIGNORE_WHERE,
				_SQL_COUNT_PRODUCTIONREADINESSIGNORE_WHERE,
				ProductionReadinessIgnoreModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "",
				new FinderColumn<>(
					"productionReadinessIgnore.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					ProductionReadinessIgnore::getCompanyId));

		_finderPathFetchByC_R = createUniqueFinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByC_R",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "ruleKey"}, 0, 2, false,
			ProductionReadinessIgnore::getCompanyId,
			convertNullFunction(ProductionReadinessIgnore::getRuleKey));

		_uniquePersistenceFinderByC_R = new UniquePersistenceFinder<>(
			this, _finderPathFetchByC_R,
			_SQL_SELECT_PRODUCTIONREADINESSIGNORE_WHERE, "",
			new FinderColumn<>(
				"productionReadinessIgnore.", "companyId",
				FinderColumn.Type.LONG, "=", true, true,
				ProductionReadinessIgnore::getCompanyId),
			new FinderColumn<>(
				"productionReadinessIgnore.", "ruleKey",
				FinderColumn.Type.STRING, "=", true, true,
				ProductionReadinessIgnore::getRuleKey));

		ProductionReadinessIgnoreUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ProductionReadinessIgnoreUtil.setPersistence(null);

		entityCache.removeCache(ProductionReadinessIgnoreImpl.class.getName());
	}

	@Override
	@Reference(
		target = PRPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = PRPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = PRPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		ProductionReadinessIgnoreModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_PRODUCTIONREADINESSIGNORE =
		"SELECT productionReadinessIgnore FROM ProductionReadinessIgnore productionReadinessIgnore";

	private static final String _SQL_SELECT_PRODUCTIONREADINESSIGNORE_WHERE =
		"SELECT productionReadinessIgnore FROM ProductionReadinessIgnore productionReadinessIgnore WHERE ";

	private static final String _SQL_COUNT_PRODUCTIONREADINESSIGNORE_WHERE =
		"SELECT COUNT(productionReadinessIgnore) FROM ProductionReadinessIgnore productionReadinessIgnore WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ProductionReadinessIgnore exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ProductionReadinessIgnorePersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1875638023