import React, { useEffect, useState } from 'react';
import { MapPin, Calendar, ExternalLink } from 'lucide-react';
import axios from 'axios';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

export default function LocalNews({ city }) {
  // city may be a string or an object { name, stateCode, … }
  const cityName = city && typeof city === 'object' ? city.name : city;
  const [articles, setArticles] = useState([]);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (!cityName) return;
    setIsLoading(true);
    const timer = setTimeout(() => {
      axios
        .get(`${API_URL}/api/articles/local/${encodeURIComponent(cityName)}`)
        .then(res => {
          setArticles(res.data);
          setIsLoading(false);
        })
        .catch(err => {
          console.error('Error fetching local news:', err);
          setArticles([]);
          setIsLoading(false);
        });
    }, 500);
    return () => clearTimeout(timer);
  }, [cityName]);

  const formatDate = iso => {
    const date = new Date(iso);
    return date.toLocaleString();
  };

  if (!cityName) {
    return null; // nothing to show until a city is selected
  }

  // Loading skeleton
  if (isLoading) {
    return (
      <div className="card w-full col-span-full md:col-span-1">
        <div className="flex items-center space-x-3 mb-6">
          <MapPin className="w-7 h-7 text-secondary" />
          <h2 className="text-2xl font-bold text-gray-800">
            Local News for {cityName}
          </h2>
        </div>
        <div className="space-y-4">
          {[1, 2, 3, 4, 5].map(i => (
            <div key={i} className="animate-pulse">
              <div className="h-5 bg-gray-200 rounded w-full mb-2"></div>
              <div className="h-3 bg-gray-200 rounded w-1/2"></div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  return (
    <div className="card w-full col-span-full md:col-span-1">
      <div className="flex items-center space-x-3 mb-6">
        <MapPin className="w-7 h-7 text-secondary" />
        <h2 className="text-2xl font-bold text-gray-800">
          Local News for {cityName}
        </h2>
      </div>
      <div className="space-y-4">
        {articles.length > 0 ? (
          articles.map(article => (
            <article key={article.url} className="group">
              <a
                href={article.url}
                className="block p-3 -mx-3 rounded-lg hover:bg-gray-50 transition-colors duration-200"
                target="_blank"
                rel="noopener noreferrer"
              >
                <h3 className="font-semibold text-lg text-gray-800 group-hover:text-primary transition-colors duration-200 leading-tight mb-1">
                  {article.title}
                </h3>
                <p className="text-sm text-gray-500">
                  <time dateTime={article.publishedAt}>{formatDate(article.publishedAt)}</time>
                </p>
              </a>
            </article>
          ))
        ) : (
          <div className="text-center py-8 text-gray-500">
            <MapPin className="w-16 h-16 mx-auto mb-4 text-gray-300" />
            <p className="text-lg">
              No local news found for{' '}
              <span className="font-semibold text-primary">{cityName}</span>. Try another city!
            </p>
          </div>
        )}
      </div>
    </div>
  );
}