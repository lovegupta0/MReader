// src/utils/libraryUtils.js

export function isRecent(lastUpdatedDate) {
  if (!lastUpdatedDate) return false;

  const updated = new Date(lastUpdatedDate);
  if (isNaN(updated.getTime())) return false;

  const TWO_DAYS_MS = 2 * 24 * 60 * 60 * 1000;
  return Date.now() - updated.getTime() <= TWO_DAYS_MS;
}

export function getCoverUrl(item) {
  // Placeholder image (safe for now)
  return `${item.baseUrl}/favicon.ico`;
}

export function getSource(baseUrl = '') {
  try {
    return new URL(baseUrl).hostname;
  } catch {
    return baseUrl;
  }
}
