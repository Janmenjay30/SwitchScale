/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx,ts,tsx}'],
  theme: {
    extend: {
      fontFamily: {
        display: ['Sora', 'sans-serif'],
        body: ['Space Grotesk', 'sans-serif'],
      },
      colors: {
        brand: {
          lime: '#E6F56A',
          ink: '#12221A',
          mint: '#CFF8DC',
          coral: '#FF9E7A',
          cream: '#FFFDF4',
        },
      },
      boxShadow: {
        card: '0 16px 40px rgba(18, 34, 26, 0.08)',
      },
      keyframes: {
        float: {
          '0%, 100%': { transform: 'translateY(0px)' },
          '50%': { transform: 'translateY(-8px)' },
        },
      },
      animation: {
        float: 'float 5s ease-in-out infinite',
      },
    },
  },
  plugins: [],
}

