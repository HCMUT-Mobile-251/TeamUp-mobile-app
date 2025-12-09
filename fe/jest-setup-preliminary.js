// Mock PixelRatio FIRST before anything else - MUST be hoisted
jest.mock('react-native/Libraries/Utilities/PixelRatio', () => {
  return {
    get: () => 2,
    getFontScale: () => 2,
    getPixelSizeForLayoutSize: (size) => size,
    roundToNearestPixel: (size) => Math.round(size),
  };
});

// Mock Dimensions
const mockDimensionsValue = { width: 375, height: 812 };
jest.mock('react-native/Libraries/Utilities/Dimensions', () => {
  return {
    get: jest.fn(() => mockDimensionsValue),
    addEventListener: jest.fn(),
    removeEventListener: jest.fn(),
  };
});