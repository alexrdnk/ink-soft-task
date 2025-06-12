import React, { useState } from 'react';
import './App.css';
import CitySearch from './components/CitySearch';
import GlobalNews from './components/GlobalNews';
import LocalNews from './components/LocalNews';

export default function App() {
  const [selectedCity, setSelectedCity] = useState('');

  return (
    <div className="App">
      <header className="header">
        <h1 className="logo">
          Public News<span className="highlight"> +</span>
        </h1>
        <CitySearch onSelectCity={setSelectedCity} />
      </header>

      <main className="flex-1 container mx-auto px-4 py-6">
        {selectedCity ? (
          <LocalNews city={selectedCity} />
        ) : (
          <GlobalNews />
        )}
      </main>

      <footer className="footer">
        <p>© 2025 Public News Plus. All rights reserved.</p>
      </footer>

    </div>
  );
}