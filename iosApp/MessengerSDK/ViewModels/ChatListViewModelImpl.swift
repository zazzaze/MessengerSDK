import Foundation
import SwiftUI
import shared

public final class ChatListViewModelImpl: BaseViewModel<ChatState, ChatEffect>, ChatListViewModel, ChatHeaderViewModel {
    @Published public var shouldScrollToFirst: Bool
    @Published public var messages: [Message] = []
    @Published public var chatTitle = ""
    @Published public var drawForeignMessageMessageTitle: Bool = false
    private var store: ChatStore

    public init(store: ChatStore) {
        shouldScrollToFirst = true
        self.store = store
        super.init()

        store.observeState().collect(collector: collector, completionHandler: {_, _ in})
        store.dispatch(action: ChatAction.Init())
    }

    override func didChangeState(_ state: ChatState?) {
        if
            let state = state,
            let resource = state.chatResource.value as? [Message]
        {
            chatTitle = state.title
            drawForeignMessageMessageTitle = state.isMultiple
            withAnimation { self.messages = resource }
        }
    }

    public func didScrollToFirst() {
        shouldScrollToFirst = false
    }

    public func sendMessage(content: String) {
        if content.isEmpty { return }
        shouldScrollToFirst = true
        store.dispatch(action: ChatAction.SendMessage(content: content))
    }

    public func didScrollToLast() {
        shouldScrollToFirst = false
        store.dispatch(action: ChatAction.NeedLoadNextBatch())
    }
}
