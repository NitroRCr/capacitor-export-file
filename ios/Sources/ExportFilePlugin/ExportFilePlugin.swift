import Foundation
import Capacitor
import UniformTypeIdentifiers

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(ExportFilePlugin)
public class ExportFilePlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "ExportFilePlugin"
    public let jsName = "ExportFile"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "exportFile", returnType: CAPPluginReturnPromise)
    ]
    private let implementation = ExportFile()

    // Store the pending call to resolve it later
    private var exportCall: CAPPluginCall?

    @objc func exportFile(_ call: CAPPluginCall) {
        guard let uri = call.getString("uri") else {
            call.reject("Must provide a URI")
            return
        }

        // Get the file URL from the URI
        guard let sourceURL = URL(string: uri) else {
            call.reject("Invalid file URI")
            return
        }

        // Check if the file exists
        guard FileManager.default.fileExists(atPath: sourceURL.path) else {
            call.reject("File does not exist at given URI")
            return
        }

        // Determine suggested filename
        let suggestedFilename = call.getString("filename") ?? sourceURL.lastPathComponent

        // Set up a temporary location with the correct file name
        let tempDir = FileManager.default.temporaryDirectory
        let tempURL = tempDir.appendingPathComponent(suggestedFilename)

        do {
            // Copy to temp if needed (Document picker works best with temp or local files)
            if sourceURL != tempURL {
                try? FileManager.default.removeItem(at: tempURL)
                try FileManager.default.copyItem(at: sourceURL, to: tempURL)
            }

            let documentPicker = UIDocumentPickerViewController(forExporting: [tempURL])
            documentPicker.shouldShowFileExtensions = true
            documentPicker.delegate = self
            documentPicker.presentationController?.delegate = self

            self.exportCall = call // Store call for later resolve

            DispatchQueue.main.async {
                self.bridge?.viewController?.present(documentPicker, animated: true, completion: nil)
            }

        } catch {
            call.reject("Failed to prepare file for export: \(error.localizedDescription)")
        }
    }
}

extension ExportFilePlugin: UIDocumentPickerDelegate, UIAdaptivePresentationControllerDelegate {
    public func documentPickerWasCancelled(_ controller: UIDocumentPickerViewController) {
        exportCall?.reject("User cancelled export")
        exportCall = nil
    }

    public func documentPicker(_ controller: UIDocumentPickerViewController, didPickDocumentsAt urls: [URL]) {
        guard let selectedURL = urls.first else {
            exportCall?.reject("No file destination selected")
            exportCall = nil
            return
        }

        // Resolve the stored plugin call with the destination URI
        exportCall?.resolve([
            "uri": selectedURL.absoluteString
        ])
        exportCall = nil
    }
}
