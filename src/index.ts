import { registerPlugin } from '@capacitor/core';

import type { ExportFilePlugin } from './definitions';

const ExportFile = registerPlugin<ExportFilePlugin>('ExportFile', {
  web: () => import('./web').then((m) => new m.ExportFileWeb()),
});

export * from './definitions';
export { ExportFile };
