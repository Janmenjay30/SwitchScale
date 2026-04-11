import { useEffect, useMemo, useState } from 'react'
import { AnimatePresence, motion as Motion } from 'framer-motion'
import {
  Clock3,
  MapPin,
  Minus,
  Plus,
  Search,
  ShoppingBag,
  Sparkles,
  Star,
  Truck,
  X,
} from 'lucide-react'
import { getMainCategories, getProductsByCategory } from './api/catalogApi'

const PRODUCT_THEMES = [
  { gradientFrom: '#FFE6C6', gradientTo: '#FFC57D', badge: 'Fresh Pick' },
  { gradientFrom: '#F8F1D1', gradientTo: '#E8DA9E', badge: 'Daily Need' },
  { gradientFrom: '#FFD5CC', gradientTo: '#FFB49F', badge: 'Best Seller' },
  { gradientFrom: '#DCC8B8', gradientTo: '#C8A88F', badge: 'Popular' },
  { gradientFrom: '#D0F1EC', gradientTo: '#9EDFD5', badge: 'Trending' },
  { gradientFrom: '#DBF4C1', gradientTo: '#B9E28D', badge: 'Top Pick' },
]

const ETA_OPTIONS = ['8 min', '9 min', '10 min', '11 min', '12 min']

function hashText(value) {
  const text = String(value || '')
  let hash = 0
  for (let index = 0; index < text.length; index += 1) {
    hash = (hash * 31 + text.charCodeAt(index)) >>> 0
  }
  return hash
}

function mapCategoryToUi(category) {
  return {
    id: category.id,
    label: category.name,
  }
}

function mapProductToUi(product) {
  const hash = hashText(product.id)
  const theme = PRODUCT_THEMES[hash % PRODUCT_THEMES.length]
  const eta = ETA_OPTIONS[hash % ETA_OPTIONS.length]
  const rating = (4 + (hash % 10) / 10).toFixed(1)

  return {
    id: product.id,
    category: product.categoryId,
    name: product.name,
    size: product.weight || '1 unit',
    price: product.price,
    mrp: product.mrp,
    eta,
    rating,
    imageUrl: product.imageUrl,
    ...theme,
  }
}

function uniqueProductsById(products) {
  const seen = new Map()
  products.forEach((product) => {
    seen.set(product.id, product)
  })
  return Array.from(seen.values())
}

const containerMotion = {
  hidden: { opacity: 0, y: 16 },
  show: {
    opacity: 1,
    y: 0,
    transition: {
      staggerChildren: 0.06,
      duration: 0.45,
      ease: [0.22, 1, 0.36, 1],
    },
  },
}

const itemMotion = {
  hidden: { opacity: 0, y: 20, scale: 0.98 },
  show: { opacity: 1, y: 0, scale: 1, transition: { duration: 0.4 } },
}

function ProductImage({ src, alt, className }) {
  const [failed, setFailed] = useState(false)

  if (!src || failed) {
    return null
  }

  return (
    <img
      src={src}
      alt={alt}
      className={className}
      loading="lazy"
      referrerPolicy="no-referrer"
      onError={() => setFailed(true)}
    />
  )
}

function App() {
  const [query, setQuery] = useState('')
  const [activeCategory, setActiveCategory] = useState('all')
  const [cart, setCart] = useState({})
  const [isCartOpen, setIsCartOpen] = useState(false)
  const [categories, setCategories] = useState([{ id: 'all', label: 'Everything' }])
  const [products, setProducts] = useState([])
  const [isLoadingCatalog, setIsLoadingCatalog] = useState(true)
  const [catalogError, setCatalogError] = useState('')

  useEffect(() => {
    let isCancelled = false

    const loadCategories = async () => {
      try {
        const backendCategories = await getMainCategories()
        if (isCancelled) {
          return
        }

        const mapped = Array.isArray(backendCategories)
          ? backendCategories.map(mapCategoryToUi)
          : []

        setCategories([{ id: 'all', label: 'Everything' }, ...mapped])
      } catch (_error) {
        if (!isCancelled) {
          setCategories([{ id: 'all', label: 'Everything' }])
          setCatalogError('Could not load categories from backend.')
        }
      }
    }

    loadCategories()

    return () => {
      isCancelled = true
    }
  }, [])

  useEffect(() => {
    let isCancelled = false

    const loadProducts = async () => {
      setIsLoadingCatalog(true)
      setCatalogError('')

      try {
        const backendCategoryIds = categories
          .filter((category) => category.id !== 'all')
          .map((category) => category.id)

        const productsFromBackend =
          activeCategory === 'all'
            ? await Promise.all(backendCategoryIds.map((categoryId) => getProductsByCategory(categoryId)))
            : [await getProductsByCategory(activeCategory)]

        if (isCancelled) {
          return
        }

        const flattened = productsFromBackend.flat().map(mapProductToUi)
        setProducts(uniqueProductsById(flattened))
      } catch (_error) {
        if (!isCancelled) {
          setProducts([])
          setCatalogError('Could not load products from backend.')
        }
      } finally {
        if (!isCancelled) {
          setIsLoadingCatalog(false)
        }
      }
    }

    loadProducts()

    return () => {
      isCancelled = true
    }
  }, [activeCategory, categories])

  const filteredProducts = useMemo(() => {
    return products.filter((product) => {
      const queryMatch =
        product.name.toLowerCase().includes(query.toLowerCase()) ||
        product.size.toLowerCase().includes(query.toLowerCase())
      return queryMatch
    })
  }, [products, query])

  const cartItems = useMemo(() => {
    return Object.entries(cart)
      .map(([productId, quantity]) => {
        const product = products.find((item) => item.id === productId)
        if (!product) {
          return null
        }
        return {
          ...product,
          quantity,
          lineTotal: product.price * quantity,
        }
      })
      .filter(Boolean)
  }, [cart])

  const totalItems = cartItems.reduce((sum, item) => sum + item.quantity, 0)
  const subtotal = cartItems.reduce((sum, item) => sum + item.lineTotal, 0)
  const deliveryFee = totalItems > 0 ? 29 : 0
  const grandTotal = subtotal + deliveryFee

  const addItem = (productId) => {
    setCart((prev) => ({
      ...prev,
      [productId]: (prev[productId] || 0) + 1,
    }))
  }

  const removeItem = (productId) => {
    setCart((prev) => {
      const current = prev[productId] || 0
      if (current <= 1) {
        const { [productId]: _removed, ...rest } = prev
        return rest
      }
      return {
        ...prev,
        [productId]: current - 1,
      }
    })
  }

  return (
    <div className="relative overflow-x-clip text-brand-ink">
      <Motion.div
        className="mx-auto flex min-h-screen w-full max-w-[1280px] flex-col px-4 pb-20 pt-5 sm:px-6 lg:px-8"
        initial="hidden"
        animate="show"
        variants={containerMotion}
      >
        <Motion.header variants={itemMotion} className="glass sticky top-4 z-30 rounded-3xl border border-black/5 px-4 py-3 shadow-card sm:px-5">
          <div className="flex flex-wrap items-center gap-3">
            <div className="rounded-2xl bg-brand-lime px-3 py-2">
              <p className="font-display text-lg font-extrabold tracking-tight">SwitchScale</p>
            </div>

            <div className="min-w-[220px] flex-1 rounded-2xl border border-black/10 bg-white px-3 py-2">
              <div className="flex items-center gap-2 text-sm text-black/55">
                <MapPin className="h-4 w-4" />
                Delivering to Sector 24, Noida
              </div>
              <p className="mt-0.5 text-xs text-black/45">Fastest slot available: 10-16 min</p>
            </div>

            <button
              type="button"
              className="ml-auto inline-flex items-center gap-2 rounded-2xl bg-brand-ink px-4 py-2 text-sm font-semibold text-white transition hover:-translate-y-0.5"
              onClick={() => setIsCartOpen(true)}
            >
              <ShoppingBag className="h-4 w-4" />
              Cart ({totalItems})
            </button>
          </div>
        </Motion.header>

        <Motion.main variants={itemMotion} className="mt-6 grid gap-6 lg:grid-cols-[1.15fr_0.85fr]">
          <section className="space-y-6">
            <div className="relative overflow-hidden rounded-3xl border border-black/10 bg-gradient-to-br from-brand-lime via-[#f8ffc6] to-brand-coral px-5 py-7 shadow-card sm:px-8 sm:py-9">
              <div className="absolute -right-8 top-4 h-32 w-32 animate-float rounded-full bg-white/45 blur-2xl" />
              <div className="absolute bottom-0 left-6 h-20 w-20 rounded-full bg-white/40 blur-xl" />

              <div className="relative max-w-2xl">
                <div className="mb-3 inline-flex items-center gap-2 rounded-full bg-white/65 px-3 py-1 text-xs font-semibold uppercase tracking-[0.18em]">
                  <Sparkles className="h-3.5 w-3.5" />
                  10 minute delivery clone
                </div>
                <h1 className="font-display text-3xl font-extrabold tracking-tight sm:text-4xl lg:text-5xl">
                  Everyday essentials. 
                  <span className="italic text-black/70">Delivered before your coffee cools.</span>
                </h1>
                <p className="mt-3 max-w-xl text-sm text-black/70 sm:text-base">
                  Blinkit-inspired shopping flow for SwitchScale with fluid interactions, modern motion, and production-ready component structure.
                </p>
              </div>
            </div>

            <div className="rounded-3xl border border-black/10 bg-white/80 p-4 shadow-card sm:p-5">
              <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                <div className="relative sm:max-w-md sm:flex-1">
                  <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-black/45" />
                  <input
                    value={query}
                    onChange={(event) => setQuery(event.target.value)}
                    placeholder="Search by product name or pack size"
                    className="w-full rounded-2xl border border-black/10 bg-white px-10 py-2.5 text-sm outline-none transition focus:border-black/30"
                  />
                </div>

                <div className="flex items-center gap-2 rounded-2xl border border-black/10 bg-white px-3 py-2 text-xs font-semibold sm:text-sm">
                  <Truck className="h-4 w-4 text-black/60" />
                  Express delivery available
                </div>
              </div>

              <div className="mask-scroll mt-4 flex gap-2 overflow-x-auto pb-1">
                {categories.map((category) => {
                  const active = activeCategory === category.id
                  return (
                    <button
                      key={category.id}
                      type="button"
                      onClick={() => setActiveCategory(category.id)}
                      className={`whitespace-nowrap rounded-full px-4 py-2 text-sm font-semibold transition ${
                        active
                          ? 'bg-brand-ink text-white'
                          : 'bg-black/5 text-black/70 hover:bg-black/10'
                      }`}
                    >
                      {category.label}
                    </button>
                  )
                })}
              </div>
            </div>

            <Motion.div layout className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
              {isLoadingCatalog && (
                <p className="col-span-full rounded-2xl border border-black/10 bg-white p-4 text-sm text-black/60">
                  Loading products...
                </p>
              )}

              {!isLoadingCatalog && catalogError && (
                <p className="col-span-full rounded-2xl border border-red-200 bg-red-50 p-4 text-sm text-red-700">
                  {catalogError}
                </p>
              )}

              <AnimatePresence>
                {filteredProducts.map((product) => {
                  const qty = cart[product.id] || 0
                  return (
                    <Motion.article
                      key={product.id}
                      layout
                      variants={itemMotion}
                      initial="hidden"
                      animate="show"
                      exit={{ opacity: 0, y: 12 }}
                      whileHover={{ y: -4 }}
                      className="rounded-3xl border border-black/10 bg-white p-4 shadow-card"
                    >
                      <div
                        className="relative mb-3 flex h-28 items-end justify-between overflow-hidden rounded-2xl p-3"
                        style={{
                          background: `linear-gradient(140deg, ${product.gradientFrom}, ${product.gradientTo})`,
                        }}
                      >
                        <ProductImage
                          src={product.imageUrl}
                          alt={product.name}
                          className="absolute inset-0 h-full w-full object-cover"
                        />
                        <div className="absolute inset-0 bg-gradient-to-t from-black/20 to-transparent" />
                        <span className="rounded-full bg-white/70 px-2 py-1 text-[10px] font-bold uppercase tracking-wider text-black/65">
                          {product.badge}
                        </span>
                        <span className="font-display text-4xl font-black leading-none text-black/25">
                          {product.name
                            .split(' ')
                            .slice(0, 2)
                            .map((word) => word[0])
                            .join('')}
                        </span>
                      </div>

                      <h3 className="font-display text-lg font-semibold leading-tight">{product.name}</h3>
                      <p className="mt-1 text-sm text-black/55">{product.size}</p>

                      <div className="mt-3 flex items-center justify-between text-sm">
                        <div className="flex items-center gap-1 text-black/60">
                          <Clock3 className="h-4 w-4" />
                          {product.eta}
                        </div>
                        <div className="flex items-center gap-1 text-amber-500">
                          <Star className="h-4 w-4 fill-current" />
                          <span className="text-black/80">{product.rating}</span>
                        </div>
                      </div>

                      <div className="mt-4 flex items-end justify-between">
                        <div>
                          <p className="text-lg font-extrabold">Rs {product.price}</p>
                          <p className="text-xs text-black/45 line-through">Rs {product.mrp}</p>
                        </div>

                        {qty === 0 ? (
                          <button
                            type="button"
                            onClick={() => addItem(product.id)}
                            className="inline-flex items-center gap-1 rounded-xl bg-brand-ink px-3 py-2 text-sm font-semibold text-white transition hover:scale-105"
                          >
                            <Plus className="h-4 w-4" /> Add
                          </button>
                        ) : (
                          <div className="inline-flex items-center gap-2 rounded-xl bg-black/5 p-1">
                            <button
                              type="button"
                              onClick={() => removeItem(product.id)}
                              className="rounded-lg bg-white p-1.5 text-black/75 hover:bg-black/10"
                              aria-label={`Decrease ${product.name}`}
                            >
                              <Minus className="h-4 w-4" />
                            </button>
                            <span className="w-5 text-center text-sm font-bold">{qty}</span>
                            <button
                              type="button"
                              onClick={() => addItem(product.id)}
                              className="rounded-lg bg-white p-1.5 text-black/75 hover:bg-black/10"
                              aria-label={`Increase ${product.name}`}
                            >
                              <Plus className="h-4 w-4" />
                            </button>
                          </div>
                        )}
                      </div>
                    </Motion.article>
                  )
                })}
              </AnimatePresence>
            </Motion.div>
          </section>

          <section className="space-y-4 lg:sticky lg:top-28 lg:h-fit">
            <div className="rounded-3xl border border-black/10 bg-white/85 p-5 shadow-card">
              <h2 className="font-display text-xl font-bold">Today at a glance</h2>
              <div className="mt-4 grid grid-cols-3 gap-3 text-center">
                <div className="rounded-2xl bg-brand-cream px-2 py-3">
                  <p className="text-2xl font-black">{products.length}</p>
                  <p className="text-xs text-black/55">SKUs</p>
                </div>
                <div className="rounded-2xl bg-brand-mint px-2 py-3">
                  <p className="text-2xl font-black">10m</p>
                  <p className="text-xs text-black/55">Avg ETA</p>
                </div>
                <div className="rounded-2xl bg-[#ffe7dd] px-2 py-3">
                  <p className="text-2xl font-black">4.5</p>
                  <p className="text-xs text-black/55">Avg rating</p>
                </div>
              </div>
            </div>

            <div className="rounded-3xl border border-black/10 bg-black px-5 py-6 text-white shadow-card">
              <p className="text-xs uppercase tracking-[0.2em] text-white/60">Membership</p>
              <h3 className="mt-2 font-display text-2xl font-bold leading-tight">SwitchScale Plus</h3>
              <p className="mt-2 text-sm text-white/75">
                Unlock free delivery and early access deals with a premium urban grocery experience.
              </p>
              <button
                type="button"
                className="mt-4 rounded-xl bg-brand-lime px-4 py-2 text-sm font-bold text-black transition hover:scale-[1.03]"
              >
                Start free trial
              </button>
            </div>
          </section>
        </Motion.main>
      </Motion.div>

      <AnimatePresence>
        {isCartOpen && (
          <>
            <Motion.button
              type="button"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              onClick={() => setIsCartOpen(false)}
              className="fixed inset-0 z-40 bg-black/25"
              aria-label="Close cart overlay"
            />

            <Motion.aside
              initial={{ x: '100%' }}
              animate={{ x: 0 }}
              exit={{ x: '100%' }}
              transition={{ type: 'spring', stiffness: 260, damping: 28 }}
              className="fixed right-0 top-0 z-50 flex h-full w-full max-w-md flex-col border-l border-black/10 bg-white"
            >
              <div className="flex items-center justify-between border-b border-black/10 px-5 py-4">
                <h3 className="font-display text-2xl font-bold">Your Cart</h3>
                <button
                  type="button"
                  onClick={() => setIsCartOpen(false)}
                  className="rounded-lg p-1.5 text-black/60 hover:bg-black/5"
                  aria-label="Close cart"
                >
                  <X className="h-5 w-5" />
                </button>
              </div>

              <div className="mask-scroll flex-1 space-y-3 overflow-y-auto px-5 py-4">
                {cartItems.length === 0 ? (
                  <div className="rounded-2xl border border-dashed border-black/20 p-6 text-center">
                    <p className="font-semibold">Your cart is empty</p>
                    <p className="mt-1 text-sm text-black/55">Add a few essentials to continue.</p>
                  </div>
                ) : (
                  cartItems.map((item) => (
                    <div key={item.id} className="rounded-2xl border border-black/10 p-3">
                      <div className="flex items-start justify-between gap-3">
                        <div className="flex items-start gap-3">
                          <div
                            className="h-12 w-12 overflow-hidden rounded-xl"
                            style={{
                              background: `linear-gradient(140deg, ${item.gradientFrom}, ${item.gradientTo})`,
                            }}
                          >
                            <ProductImage
                              src={item.imageUrl}
                              alt={item.name}
                              className="h-full w-full object-cover"
                            />
                          </div>
                          <div>
                            <p className="font-semibold leading-tight">{item.name}</p>
                            <p className="mt-1 text-xs text-black/55">{item.size}</p>
                          </div>
                        </div>
                        <p className="text-sm font-bold">Rs {item.lineTotal}</p>
                      </div>
                      <div className="mt-3 flex items-center justify-between">
                        <div className="inline-flex items-center gap-2 rounded-xl bg-black/5 p-1">
                          <button
                            type="button"
                            onClick={() => removeItem(item.id)}
                            className="rounded-lg bg-white p-1.5 text-black/75"
                          >
                            <Minus className="h-4 w-4" />
                          </button>
                          <span className="w-5 text-center text-sm font-bold">{item.quantity}</span>
                          <button
                            type="button"
                            onClick={() => addItem(item.id)}
                            className="rounded-lg bg-white p-1.5 text-black/75"
                          >
                            <Plus className="h-4 w-4" />
                          </button>
                        </div>
                      </div>
                    </div>
                  ))
                )}
              </div>

              <div className="border-t border-black/10 px-5 py-4">
                <div className="space-y-2 text-sm">
                  <div className="flex justify-between">
                    <span className="text-black/60">Subtotal</span>
                    <span className="font-semibold">Rs {subtotal}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-black/60">Delivery fee</span>
                    <span className="font-semibold">Rs {deliveryFee}</span>
                  </div>
                  <div className="flex justify-between border-t border-black/10 pt-2 text-base">
                    <span className="font-bold">Total</span>
                    <span className="font-black">Rs {grandTotal}</span>
                  </div>
                </div>

                <button
                  type="button"
                  className="mt-4 w-full rounded-xl bg-brand-ink px-4 py-3 text-sm font-semibold text-white transition enabled:hover:scale-[1.02] disabled:cursor-not-allowed disabled:opacity-50"
                  disabled={totalItems === 0}
                >
                  Proceed to checkout
                </button>
              </div>
            </Motion.aside>
          </>
        )}
      </AnimatePresence>
    </div>
  )
}

export default App
