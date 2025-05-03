export interface ExportFilePlugin {
  exportFile(options: { 
    uri: string, 
    filename?: string,
  }): Promise<{ uri: string }>;
}
