import { NavLink, Route, Routes } from 'react-router-dom';
import { LoginPage } from './pages/LoginPage';
import { CatalogPage } from './pages/CatalogPage';
import { RecommendationsPage } from './pages/RecommendationsPage';
import { ProfilePage } from './pages/ProfilePage';

const navBase = 'rounded-md px-3 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-200';
const navActive = 'bg-indigo-600 text-white hover:bg-indigo-600';

export function App() {
  return (
    <div className="mx-auto max-w-6xl p-6">
      <header className="mb-6 flex flex-col gap-4 rounded-xl bg-white p-4 shadow-sm md:flex-row md:items-center md:justify-between">
        <h1 className="text-2xl font-bold text-slate-900">Library Preferences System</h1>
        <nav className="flex flex-wrap gap-2">
          <NavLink className={({ isActive }) => `${navBase} ${isActive ? navActive : ''}`} to="/login">
            Login
          </NavLink>
          <NavLink className={({ isActive }) => `${navBase} ${isActive ? navActive : ''}`} to="/catalog">
            Catalog
          </NavLink>
          <NavLink className={({ isActive }) => `${navBase} ${isActive ? navActive : ''}`} to="/recommendations">
            Recommendations
          </NavLink>
          <NavLink className={({ isActive }) => `${navBase} ${isActive ? navActive : ''}`} to="/profile">
            Profile
          </NavLink>
        </nav>
      </header>

      <main>
        <Routes>
          <Route path="/" element={<CatalogPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/catalog" element={<CatalogPage />} />
          <Route path="/recommendations" element={<RecommendationsPage />} />
          <Route path="/profile" element={<ProfilePage />} />
        </Routes>
      </main>
    </div>
  );
}
