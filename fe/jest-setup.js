// Mock NativeEventEmitter
jest.mock('react-native/Libraries/EventEmitter/NativeEventEmitter', () => {
  return jest.fn().mockImplementation(() => ({
    addListener: jest.fn(),
    removeListener: jest.fn(),
    removeAllListeners: jest.fn(),
  }));
});

// Extend jest-native matchers
import '@testing-library/jest-native/extend-expect';

// Override Dimensions after react-native is loaded
const ReactNative = require('react-native');
const mockDimensionsValue = { width: 375, height: 812 };
if (ReactNative.Dimensions) {
  ReactNative.Dimensions.get = jest.fn(() => mockDimensionsValue);
  ReactNative.Dimensions.addEventListener = jest.fn();
  ReactNative.Dimensions.removeEventListener = jest.fn();
}

// Mock các dependencies khác
jest.mock('react-native-gesture-handler', () => { /* ... */ });
jest.mock('@react-native-async-storage/async-storage', () => { /* ... */ });
jest.mock('react-native-reanimated', () => { /* ... */ });
jest.mock('expo-linear-gradient', () => { /* ... */ });
jest.mock('@expo/vector-icons', () => { /* ... */ });
jest.mock('react-native-safe-area-context', () => { /* ... */ });