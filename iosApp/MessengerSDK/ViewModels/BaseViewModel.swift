import Foundation
import SwiftUI
import shared

public class BaseViewModel<State, Effect>: ObservableObject {
    lazy var collector: Observer = {
        let collector = Observer { [weak self] state in
            if let value = state as? State? {
                self?.didChangeState(value)
            }
        }
        return collector
    }()

    lazy var effectCollector: Observer = {
        let collector = Observer { [weak self] effect in
            if let value = effect as? Effect? {
                self?.didRecieveEffect(value)
            }
        }
        return collector
    }()

    func didChangeState(_ state: State?) {  }

    func didRecieveEffect(_ effect: Effect?) {  }
}

typealias Collector = Kotlinx_coroutines_coreFlowCollector

class Observer: Collector {
    let callback:(Any?) -> Void

    init(callback: @escaping (Any?) -> Void) {
        self.callback = callback
    }

    func emit(value: Any?, completionHandler: @escaping (KotlinUnit?, Error?) -> Void) {
        callback(value)
        completionHandler(KotlinUnit(), nil)
    }
}

