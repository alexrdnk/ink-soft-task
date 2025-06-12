import React, { useEffect, useState } from 'react';
import axios from 'axios';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

export default function GlobalNews() {
  const [articles, setArticles] = useState([]);
  const [isLoading, setIsLoading] = useState(true); // Add loading state

  useEffect(() => {
    setIsLoading(true); // Set loading true on effect run
    axios.get(`${API_URL}/api/articles/global`)
      .then(res => {
        setArticles(res.data);
        setIsLoading(false); // Set loading false on success
      })
      .catch(error => {
        console.error('Error fetching global news:', error);
        setIsLoading(false); // Set loading false on error
      });
  }, []);

  if (isLoading) {
    return (
      <div className="card w-full col-span-full md:col-span-1"> {/* Apply card class and full width on mobile */}
        <h2 className="section-title">Global News</h2>
        <div className="space-y-4">
          {[1, 2, 3, 4, 5].map(i => ( // More loading skeletons
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
    <div className="card w-full col-span-full md:col-span-1"> {/* Apply card class and full width on mobile */}
      <h2 className="section-title">Global News</h2>
      <div className="space-y-4"> {/* Increased space between articles */}
        {articles.length > 0 ? (
          articles.map(a => (
            <article key={a.id} className="group"> {/* Use article tag for semantic HTML */}
              <a
                href={a.url}
                className="block p-3 -mx-3 rounded-lg hover:bg-gray-50 transition-colors duration-200"
                target="_blank"
                rel="noopener noreferrer"
              >
                <h3 className="font-semibold text-lg text-gray-800 group-hover:text-primary transition-colors duration-200 leading-tight mb-1"> {/* Improved heading */}
                  {a.title}
                </h3>
                <p className="text-sm text-gray-500">
                  <time dateTime={a.publishedAt}>{new Date(a.publishedAt).toLocaleString()}</time> {/* Use time tag */}
                </p>
              </a>
            </article>
          ))
        ) : (
          <div className="text-center py-8 text-gray-500">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth="1.5" stroke="currentColor" className="w-12 h-12 mx-auto mb-3 text-gray-300">
              <path strokeLinecap="round" strokeLinejoin="round" d="M12 7.5h1.5m-1.5 3h1.5m-7.5 3h7.5m-7.5 3h7.5m3-9h3.375c.621 0 1.125.504 1.125 1.125V18a2.25 2.25 0 01-2.25 2.25M16.5 7.5V18a2.25 2.25 0 002.25 2.25H21M7.5 12h-1.5m0 3.75H5.25A2.25 2.25 0 013 15.75V15m1.5 0v-3.75m-1.5 3.75h1.5m-1.5 0h3.75v-3.75m-7.5 0h3.75v-3.75m-7.5 0H7.5m-7.5 0H12m-7.5 0v-3.75m0 0H7.5m0 0H12m-7.5 0V3.75C4.5 3.129 4.904 2.625 5.525 2.625H12" />
            </svg>
            <p>No global news available at the moment.</p>
          </div>
        )}
      </div>
    </div>
  );


  return (
    <div className="global-news-container">
      <div className="card news-card">
        <h2 className="section-title">Global News</h2>
        <div className="space-y-4">
          {isLoading ? (
            [...Array(5)].map((_, i) => (
              <div key={i} className="animate-pulse">
                <div className="h-5 bg-gray-200 rounded w-full mb-2"></div>
                <div className="h-3 bg-gray-200 rounded w-1/2"></div>
              </div>
            ))
          ) : (
            articles.map(a => (
              <article key={a.id} className="group">
                {/* содержание статьи */}
              </article>
            ))
          )}
        </div>
      </div>
    </div>
  );

}