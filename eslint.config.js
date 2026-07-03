import js from '@eslint/js';
import tsPlugin from '@typescript-eslint/eslint-plugin';
import tsParser from '@typescript-eslint/parser';

/** ESLint flat config — dùng kèm `npm run lint:eslint` (SunLint + ESLint). */
export default [
  js.configs.recommended,
  {
    files: ['theme-kit/**/*.{ts,tsx}', 'src/**/*.{ts,tsx,js}'],
    languageOptions: {
      parser: tsParser,
      parserOptions: {
        ecmaVersion: 'latest',
        sourceType: 'module',
        ecmaFeatures: { jsx: true },
      },
    },
    plugins: { '@typescript-eslint': tsPlugin },
    rules: {
      ...tsPlugin.configs.recommended.rules,
    },
  },
  {
    ignores: ['node_modules/**', 'target/**', '.mvn/**'],
  },
];
