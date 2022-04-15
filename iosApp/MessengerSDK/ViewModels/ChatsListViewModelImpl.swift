import Foundation
import shared

public final class ChatsListViewModelImpl: BaseViewModel<ChatListState, ChatListEffect>, ChatsListViewModel {
    @Published public var chatsList: [Chat] = []
    let multiple: Bool

    private let store: ChatListStore

    init(store: ChatListStore, multiple: Bool) {
        self.store = store
        self.multiple = multiple
        super.init()

        store.observeState().collect(collector: collector, completionHandler: {_, _ in})
        store.dispatch(action: ChatListAction.Init())
    }

    override func didChangeState(_ state: ChatListState?) {
        if
            let state = state,
            let chatsList = state.chatListResource.value as? [Chat]
        {
            if multiple {
                self.chatsList = chatsList.filter { $0 is Chat.MultipleChat }
            }
            else {
                self.chatsList = chatsList.filter { $0 is Chat.SimpleChat }
            }
        }
    }
}
