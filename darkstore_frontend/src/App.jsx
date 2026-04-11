import { useEffect, useMemo, useState } from 'react'
import {
  AlertTriangle,
  CheckCircle2,
  PackageSearch,
  Pencil,
  Plus,
  RefreshCw,
  Search,
  Trash2,
  X,
} from 'lucide-react'
import {
  createCategory,
  createProduct,
  deleteProduct,
  getMainCategories,
  getProductsByCategory,
  updateProduct,
} from './api/catalogApi'

const ETA_STORAGE_KEY = 'darkstore_eta_by_id'
const HIDDEN_STORAGE_KEY = 'darkstore_hidden_product_ids'

const EMPTY_FORM = {
  id: '',
  categoryId: '',
  name: '',
  description: '',
  price: '',
  mrp: '',
  weight: '',
  imageUrl: '',
  eta: '10',
  isActive: true,
}

const EMPTY_CATEGORY_FORM = {
  name: '',
  imageUrl: '',
  parentId: '',
}

function ProductImage({ src, alt, className }) {
  const [failed, setFailed] = useState(false)

  if (!src || failed) {
    return <div className={`${className} image-placeholder`}>No image</div>
  }

  return (
    <img
      src={src}
      alt={alt}
      className={className}
      loading="lazy"
      onError={() => setFailed(true)}
      referrerPolicy="no-referrer"
    />
  )
}

function elasticSearchCategories(categories, query) {
  if (!query.trim()) {
    return categories.slice(0, 8)
  }

  const normalizedQuery = query.trim().toLowerCase()
  const queryTokens = normalizedQuery.split(/\s+/).filter(Boolean)

  const ranked = categories
    .map((category) => {
      const name = (category.name || '').toLowerCase()
      if (!name) {
        return null
      }

      let score = 0

      if (name === normalizedQuery) {
        score += 130
      }

      if (name.startsWith(normalizedQuery)) {
        score += 90
      }

      if (name.includes(normalizedQuery)) {
        score += 60
      }

      queryTokens.forEach((token) => {
        if (name.startsWith(token)) {
          score += 28
        }
        if (name.includes(token)) {
          score += 16
        }
      })

      // Keep near matches discoverable by rewarding in-order character matches.
      let pointer = 0
      for (let index = 0; index < normalizedQuery.length; index += 1) {
        const char = normalizedQuery[index]
        const foundAt = name.indexOf(char, pointer)
        if (foundAt === -1) {
          break
        }
        score += 3
        pointer = foundAt + 1
      }

      return {
        category,
        score,
      }
    })
    .filter(Boolean)
    .filter((entry) => entry.score > 0)
    .sort((left, right) => {
      if (right.score !== left.score) {
        return right.score - left.score
      }
      return left.category.name.localeCompare(right.category.name)
    })

  return ranked.slice(0, 8).map((entry) => entry.category)
}

function readJsonStorage(key, fallbackValue) {
  try {
    const raw = window.localStorage.getItem(key)
    return raw ? JSON.parse(raw) : fallbackValue
  } catch {
    return fallbackValue
  }
}

function App() {
  const [categories, setCategories] = useState([])
  const [products, setProducts] = useState([])
  const [selectedCategory, setSelectedCategory] = useState('all')
  const [query, setQuery] = useState('')
  const [form, setForm] = useState(EMPTY_FORM)
  const [mode, setMode] = useState('create')
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [categorySaving, setCategorySaving] = useState(false)
  const [message, setMessage] = useState(null)
  const [categoryForm, setCategoryForm] = useState(EMPTY_CATEGORY_FORM)
  const [parentCategoryQuery, setParentCategoryQuery] = useState('')
  const [isParentPickerOpen, setIsParentPickerOpen] = useState(false)
  const [etaById, setEtaById] = useState(() => readJsonStorage(ETA_STORAGE_KEY, {}))
  const [hiddenProductIds, setHiddenProductIds] = useState(() => readJsonStorage(HIDDEN_STORAGE_KEY, []))

  useEffect(() => {
    window.localStorage.setItem(ETA_STORAGE_KEY, JSON.stringify(etaById))
  }, [etaById])

  useEffect(() => {
    window.localStorage.setItem(HIDDEN_STORAGE_KEY, JSON.stringify(hiddenProductIds))
  }, [hiddenProductIds])

  const loadCatalog = async () => {
    setLoading(true)
    setMessage(null)
    try {
      const loadedCategories = await getMainCategories()
      setCategories(loadedCategories)

      const categoryIds = loadedCategories.map((category) => category.id)
      const categoryProducts = await Promise.all(
        categoryIds.map((categoryId) => getProductsByCategory(categoryId)),
      )

      const flattened = categoryProducts.flat()
      const unique = Array.from(new Map(flattened.map((product) => [product.id, product])).values())
      setProducts(unique)

      if (!form.categoryId && loadedCategories.length > 0) {
        setForm((prev) => ({ ...prev, categoryId: loadedCategories[0].id }))
      }
    } catch (error) {
      setMessage({
        type: 'error',
        text: error.message || 'Unable to load catalog data.',
      })
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadCatalog()
  }, [])

  const visibleProducts = useMemo(() => {
    const hiddenSet = new Set(hiddenProductIds)
    const searchTerm = query.trim().toLowerCase()

    return products
      .filter((product) => !hiddenSet.has(product.id))
      .filter((product) => selectedCategory === 'all' || product.categoryId === selectedCategory)
      .filter((product) => {
        if (!searchTerm) {
          return true
        }

        return (
          product.name.toLowerCase().includes(searchTerm) ||
          (product.description || '').toLowerCase().includes(searchTerm) ||
          product.id.toLowerCase().includes(searchTerm)
        )
      })
      .map((product) => ({
        ...product,
        eta: etaById[product.id] || 10,
      }))
      .sort((left, right) => left.name.localeCompare(right.name))
  }, [products, hiddenProductIds, selectedCategory, query, etaById])

  const selectedParentCategory = useMemo(
    () => categories.find((category) => category.id === categoryForm.parentId) || null,
    [categories, categoryForm.parentId],
  )

  const searchableParentCategories = useMemo(
    () => categories.filter((category) => category.parentId == null),
    [categories],
  )

  const parentCategoryMatches = useMemo(
    () => elasticSearchCategories(searchableParentCategories, parentCategoryQuery),
    [searchableParentCategories, parentCategoryQuery],
  )

  const startCreate = () => {
    setMode('create')
    setForm((prev) => ({
      ...EMPTY_FORM,
      categoryId: prev.categoryId || categories[0]?.id || '',
    }))
  }

  const startEdit = (product) => {
    setMode('edit')
    setForm({
      id: product.id,
      categoryId: product.categoryId,
      name: product.name,
      description: product.description || '',
      price: String(product.price),
      mrp: String(product.mrp),
      weight: product.weight,
      imageUrl: product.imageUrl || '',
      eta: String(etaById[product.id] || 10),
      isActive: !hiddenProductIds.includes(product.id),
    })
  }

  const updateFormValue = (field, value) => {
    setForm((prev) => ({
      ...prev,
      [field]: value,
    }))
  }

  const updateCategoryFormValue = (field, value) => {
    setCategoryForm((prev) => ({
      ...prev,
      [field]: value,
    }))
  }

  const handleCategorySubmit = async (event) => {
    event.preventDefault()

    const name = categoryForm.name.trim()
    if (!name) {
      setMessage({ type: 'error', text: 'Category name is required.' })
      return
    }

    const payload = {
      name,
      imageUrl: categoryForm.imageUrl.trim(),
      parentId: categoryForm.parentId.trim() || null,
    }

    setCategorySaving(true)
    setMessage(null)

    try {
      const created = await createCategory(payload)
      setCategories((prev) => [created, ...prev])
      setForm((prev) => ({
        ...prev,
        categoryId: created.id,
      }))
      setSelectedCategory(created.id)
      setCategoryForm(EMPTY_CATEGORY_FORM)
      setParentCategoryQuery('')
      setIsParentPickerOpen(false)
      setMessage({ type: 'success', text: `Category "${created.name}" created successfully.` })
    } catch (error) {
      setMessage({ type: 'error', text: error.message || 'Category creation failed.' })
    } finally {
      setCategorySaving(false)
    }
  }

  const handleParentCategoryPick = (category) => {
    setCategoryForm((prev) => ({
      ...prev,
      parentId: category.id,
    }))
    setParentCategoryQuery(category.name)
    setIsParentPickerOpen(false)
  }

  const clearParentCategory = () => {
    setCategoryForm((prev) => ({
      ...prev,
      parentId: '',
    }))
    setParentCategoryQuery('')
    setIsParentPickerOpen(false)
  }

  const handleSubmit = async (event) => {
    event.preventDefault()

    const price = Number(form.price)
    const mrp = Number(form.mrp)
    const eta = Number(form.eta)

    if (!form.categoryId || !form.name || !form.weight) {
      setMessage({ type: 'error', text: 'Category, name, and weight are required.' })
      return
    }

    if (!Number.isFinite(price) || !Number.isFinite(mrp) || price <= 0 || mrp <= 0) {
      setMessage({ type: 'error', text: 'Price and MRP must be valid numbers greater than zero.' })
      return
    }

    if (!Number.isFinite(eta) || eta < 1) {
      setMessage({ type: 'error', text: 'ETA must be at least 1 minute.' })
      return
    }

    const payload = {
      categoryId: form.categoryId,
      name: form.name.trim(),
      description: form.description.trim(),
      price,
      mrp,
      weight: form.weight.trim(),
      imageUrl: form.imageUrl.trim(),
      isActive: Boolean(form.isActive),
    }

    setSaving(true)
    setMessage(null)

    if (mode === 'create') {
      try {
        const created = await createProduct(payload)
        setProducts((prev) => [created, ...prev])
        setEtaById((prev) => ({ ...prev, [created.id]: eta }))
        setMessage({ type: 'success', text: 'Product created successfully.' })
        startCreate()
      } catch (error) {
        setMessage({
          type: 'error',
          text: error.message || 'Product creation failed.',
        })
      } finally {
        setSaving(false)
      }
      return
    }

    const productId = form.id

    setProducts((prev) =>
      prev.map((product) =>
        product.id === productId
          ? {
              ...product,
              ...payload,
            }
          : product,
      ),
    )
    setEtaById((prev) => ({
      ...prev,
      [productId]: eta,
    }))

    if (form.isActive) {
      setHiddenProductIds((prev) => prev.filter((id) => id !== productId))
    } else {
      setHiddenProductIds((prev) => (prev.includes(productId) ? prev : [...prev, productId]))
    }

    try {
      const updated = await updateProduct(productId, payload)
      setProducts((prev) =>
        prev.map((product) => (product.id === productId ? { ...product, ...updated } : product)),
      )
      setMessage({ type: 'success', text: 'Product updated successfully.' })
    } catch (error) {
      setMessage({
        type: 'warning',
        text:
          error.message ||
          'Backend update endpoint is not available. Changes are applied only in this manager UI.',
      })
    } finally {
      setSaving(false)
      startCreate()
    }
  }

  const handleQuickEtaChange = (productId, value) => {
    const eta = Number(value)
    if (!Number.isFinite(eta) || eta < 1) {
      return
    }
    setEtaById((prev) => ({
      ...prev,
      [productId]: eta,
    }))
  }

  const handleRemove = async (product) => {
    const approved = window.confirm(`Remove ${product.name} from the manager list?`)
    if (!approved) {
      return
    }

    setHiddenProductIds((prev) => (prev.includes(product.id) ? prev : [...prev, product.id]))

    try {
      await deleteProduct(product.id)
      setProducts((prev) => prev.filter((item) => item.id !== product.id))
      setMessage({ type: 'success', text: `${product.name} removed from backend.` })
    } catch (error) {
      setMessage({
        type: 'warning',
        text:
          error.message ||
          'Backend delete endpoint is not available. Product was hidden from manager view only.',
      })
    }
  }

  const activeCount = visibleProducts.length

  return (
    <div className="manager-shell">
      <header className="manager-topbar">
        <div>
          <p className="eyebrow">Darkstore Console</p>
          <h1>Product Operations Desk</h1>
          <p className="subcopy">Create, update, remove products and tune ETA for store operations.</p>
        </div>
        <button type="button" className="ghost-btn" onClick={loadCatalog} disabled={loading}>
          <RefreshCw size={16} /> Refresh
        </button>
      </header>

      {message && (
        <div className={`notice notice-${message.type}`}>
          {message.type === 'error' && <AlertTriangle size={16} />}
          {message.type === 'success' && <CheckCircle2 size={16} />}
          {message.type === 'warning' && <AlertTriangle size={16} />}
          <span>{message.text}</span>
        </div>
      )}

      <main className="manager-grid">
        <section className="panel form-panel">
          <div className="panel-head">
            <h2>Add Category</h2>
          </div>

          <form onSubmit={handleCategorySubmit} className="category-form">
            <label>
              Category name
              <input
                value={categoryForm.name}
                onChange={(event) => updateCategoryFormValue('name', event.target.value)}
                placeholder="Fresh Vegetables"
                required
              />
            </label>

            <label>
              Category image URL
              <input
                value={categoryForm.imageUrl}
                onChange={(event) => updateCategoryFormValue('imageUrl', event.target.value)}
                placeholder="https://example.com/category.png"
              />
            </label>

            <label>
              Parent category (search by name)
              <div className="parent-picker">
                <div className="parent-input-wrap">
                  <Search size={16} />
                  <input
                    value={parentCategoryQuery}
                    onChange={(event) => {
                      setParentCategoryQuery(event.target.value)
                      setIsParentPickerOpen(true)
                    }}
                    onFocus={() => setIsParentPickerOpen(true)}
                    placeholder="Type to search existing parent categories"
                  />
                  {!!categoryForm.parentId && (
                    <button
                      type="button"
                      className="inline-clear-btn"
                      onClick={clearParentCategory}
                      aria-label="Clear parent category"
                    >
                      <X size={15} />
                    </button>
                  )}
                </div>

                {isParentPickerOpen && parentCategoryMatches.length > 0 && (
                  <div className="parent-results">
                    {parentCategoryMatches.map((category) => (
                      <button
                        type="button"
                        key={category.id}
                        className="parent-result-item"
                        onClick={() => handleParentCategoryPick(category)}
                      >
                        <span>{category.name}</span>
                        <small>{category.id}</small>
                      </button>
                    ))}
                  </div>
                )}
              </div>
            </label>

            <div className="parent-chip-row">
              {selectedParentCategory ? (
                <p className="selected-parent-chip">
                  Selected parent: <strong>{selectedParentCategory.name}</strong>
                </p>
              ) : (
                <p className="selected-parent-chip muted">No parent selected (this will be a main category)</p>
              )}
            </div>

            <button type="submit" className="primary-btn" disabled={categorySaving}>
              <Plus size={16} />
              {categorySaving ? 'Adding...' : 'Add Category'}
            </button>
          </form>

          <div className="form-divider" />

          <div className="panel-head">
            <h2>{mode === 'create' ? 'Add Product' : 'Edit Product'}</h2>
            {mode === 'edit' && (
              <button type="button" className="link-btn" onClick={startCreate}>
                Cancel edit
              </button>
            )}
          </div>

          <form onSubmit={handleSubmit} className="product-form">
            <label>
              Category
              <select
                value={form.categoryId}
                onChange={(event) => updateFormValue('categoryId', event.target.value)}
                required
              >
                <option value="">Select category</option>
                {categories.map((category) => (
                  <option key={category.id} value={category.id}>
                    {category.name}
                  </option>
                ))}
              </select>
            </label>

            <label>
              Product name
              <input
                value={form.name}
                onChange={(event) => updateFormValue('name', event.target.value)}
                placeholder="Farm Fresh Tomato"
                required
              />
            </label>

            <label>
              Description
              <textarea
                value={form.description}
                onChange={(event) => updateFormValue('description', event.target.value)}
                placeholder="Short manager-facing description"
                rows={3}
              />
            </label>

            <div className="form-row">
              <label>
                Price
                <input
                  type="number"
                  min="1"
                  step="0.01"
                  value={form.price}
                  onChange={(event) => updateFormValue('price', event.target.value)}
                  required
                />
              </label>
              <label>
                MRP
                <input
                  type="number"
                  min="1"
                  step="0.01"
                  value={form.mrp}
                  onChange={(event) => updateFormValue('mrp', event.target.value)}
                  required
                />
              </label>
            </div>

            <div className="form-row">
              <label>
                Weight/Pack
                <input
                  value={form.weight}
                  onChange={(event) => updateFormValue('weight', event.target.value)}
                  placeholder="500 g"
                  required
                />
              </label>
              <label>
                ETA (mins)
                <input
                  type="number"
                  min="1"
                  value={form.eta}
                  onChange={(event) => updateFormValue('eta', event.target.value)}
                  required
                />
              </label>
            </div>

            <label>
              Image URL
              <input
                value={form.imageUrl}
                onChange={(event) => updateFormValue('imageUrl', event.target.value)}
                placeholder="https://example.com/image.png"
              />
            </label>

            <div className="inline-image-preview">
              <ProductImage
                src={form.imageUrl.trim()}
                alt={form.name || 'Product preview'}
                className="form-preview-image"
              />
            </div>

            <label className="checkbox-field">
              <input
                type="checkbox"
                checked={form.isActive}
                onChange={(event) => updateFormValue('isActive', event.target.checked)}
              />
              Product is active
            </label>

            <button type="submit" className="primary-btn" disabled={saving || categories.length === 0}>
              {mode === 'create' ? <Plus size={16} /> : <CheckCircle2 size={16} />}
              {saving ? 'Saving...' : mode === 'create' ? 'Add Product' : 'Save Changes'}
            </button>
          </form>
        </section>

        <section className="panel list-panel">
          <div className="panel-head">
            <h2>
              Product Queue <span>{activeCount}</span>
            </h2>
          </div>

          <div className="list-controls">
            <label>
              Category
              <select value={selectedCategory} onChange={(event) => setSelectedCategory(event.target.value)}>
                <option value="all">All categories</option>
                {categories.map((category) => (
                  <option key={category.id} value={category.id}>
                    {category.name}
                  </option>
                ))}
              </select>
            </label>
            <label>
              Search
              <input
                value={query}
                onChange={(event) => setQuery(event.target.value)}
                placeholder="Search by name, id, description"
              />
            </label>
          </div>

          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Image</th>
                  <th>Name</th>
                  <th>Category</th>
                  <th>Price</th>
                  <th>ETA</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {loading && (
                  <tr>
                    <td colSpan={6} className="empty-state">
                      Loading products...
                    </td>
                  </tr>
                )}

                {!loading && visibleProducts.length === 0 && (
                  <tr>
                    <td colSpan={6} className="empty-state">
                      <PackageSearch size={16} />
                      <span>No products match this view.</span>
                    </td>
                  </tr>
                )}

                {visibleProducts.map((product) => (
                  <tr key={product.id}>
                    <td>
                      <ProductImage
                        src={product.imageUrl}
                        alt={product.name}
                        className="table-product-image"
                      />
                    </td>
                    <td>
                      <p className="name-cell">{product.name}</p>
                      <p className="meta-cell">{product.weight}</p>
                    </td>
                    <td>{categories.find((category) => category.id === product.categoryId)?.name || '-'}</td>
                    <td>
                      Rs {product.price} <span className="meta-cell">(MRP Rs {product.mrp})</span>
                    </td>
                    <td>
                      <input
                        type="number"
                        min="1"
                        className="eta-input"
                        value={etaById[product.id] || 10}
                        onChange={(event) => handleQuickEtaChange(product.id, event.target.value)}
                      />
                    </td>
                    <td>
                      <div className="actions">
                        <button type="button" className="icon-btn" onClick={() => startEdit(product)}>
                          <Pencil size={15} /> Edit
                        </button>
                        <button type="button" className="icon-btn danger" onClick={() => handleRemove(product)}>
                          <Trash2 size={15} /> Remove
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>
      </main>
    </div>
  )
}

export default App
