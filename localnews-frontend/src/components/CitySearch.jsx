import React, { useState, useEffect, useRef } from 'react';
import axios from 'axios';
import { X, Search } from 'lucide-react';
import './CitySearch.css';

export default function CitySearch({ onSelectCity }) {
  const [query, setQuery] = useState('');
  const [suggestions, setSuggestions] = useState([]);            // now City objects
  const [showDropdown, setShowDropdown] = useState(false);
  const wrapperRef = useRef(null);
  const debounceRef = useRef(null);

  // Fetch suggestions with debounce
  useEffect(() => {
    if (query.length < 1) {
      setSuggestions([]);
      setShowDropdown(false);
      return;
    }

    clearTimeout(debounceRef.current);
    debounceRef.current = setTimeout(() => {
      // after (correct)
        axios.get(`/api/cities?prefix=${encodeURIComponent(query)}`)
        .then(res => {
          // res.data is assumed to be City[]
          setSuggestions(res.data.slice(0, 5));
        })
        .catch(err => {
          console.error('Error fetching city suggestions:', err);
          setSuggestions([]);
        });
    }, 300);
  }, [query]);

  // Show dropdown when suggestions are available
  useEffect(() => {
    setShowDropdown(suggestions.length > 0);
  }, [suggestions]);

  // Close dropdown on outside click
  useEffect(() => {
    const handleClickOutside = e => {
      if (wrapperRef.current && !wrapperRef.current.contains(e.target)) {
        setShowDropdown(false);
      }
    };
    document.addEventListener('click', handleClickOutside);
    return () => document.removeEventListener('click', handleClickOutside);
  }, []);

  const handleSelect = cityObj => {
    setQuery(cityObj.name);
    onSelectCity(cityObj);               // send full object back
    setSuggestions([]);
    setShowDropdown(false);
  };

  const handleKeyDown = e => {
    if (e.key === 'Enter') {
      e.preventDefault();
      if (query.trim()) {
        // If they hit enter without clicking suggestion, try to find matching object
        const match = suggestions.find(c =>
          c.name.toLowerCase() === query.trim().toLowerCase()
        );
        handleSelect(match || { name: query.trim() });
      }
    }
  };

  const clearSearch = () => {
    setQuery('');
    setSuggestions([]);
    onSelectCity(null);
  };

  const renderSuggestion = (cityName) => {
    const idx = cityName.toLowerCase().indexOf(query.toLowerCase());
    if (idx === -1) return <>{cityName}</>;
    return (
      <>
        {cityName.slice(0, idx)}
        <span className="highlight">{cityName.slice(idx, idx + query.length)}</span>
        {cityName.slice(idx + query.length)}
      </>
    );
  };

  return (
    <div className="city-search" ref={wrapperRef}>
      <div className="input-wrapper">
        <Search className="icon search-icon" />
        <input
          type="text"
          placeholder="Search"
          value={query}
          onChange={e => setQuery(e.target.value)}
          className="city-input"
          onFocus={() => setShowDropdown(suggestions.length > 0)}
          onKeyDown={handleKeyDown}
        />
        {query && (
          <button type="button" className="clear-btn" onClick={clearSearch}>
            <X className="icon clear-icon" />
          </button>
        )}
      </div>
      {showDropdown && (
        <ul className="suggestions-list">
          {suggestions.map(cityObj => (
            <li
              key={`${cityObj.name}-${cityObj.stateCode}`}
              className="suggestion-item"
              onClick={() => handleSelect(cityObj)}
            >
              <div className="suggestion-content">
                {renderSuggestion(cityObj.name)}
                <span className="state-code">, {cityObj.stateCode}</span>
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}