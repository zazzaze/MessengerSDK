import SwiftUI
import SwiftUIRouter
import shared

public struct MainView: View {
    let singleViewModel: ChatsListViewModelImpl
    let multipleViewModel: ChatsListViewModelImpl

    public var body: some View {
        Router {
            RootTabBar(singleViewModel: singleViewModel, multipleViewModel: multipleViewModel)
        }
    }
}

struct RootTabBar: View {
    @EnvironmentObject private var navigator: Navigator
    let singleViewModel: ChatsListViewModelImpl
    let multipleViewModel: ChatsListViewModelImpl
    @State var selectedIndex = 0
    private let tabsBackground = Color(red: 244, green: 244, blue: 244)
    var body: some View {
        GeometryReader { geometry in
            VStack {
                SwitchRoutes {
                    Route("chat/:chatId/*", validator: findChat) {
                        viewModel in
                            ChatView(viewModel: viewModel)
                            .swipeableBackAlt()
                    }

                    Route {
                        VStack {
                            Tabs(
                                tabs: .constant(["Personal", "Teams"]),
                                selection: $selectedIndex,
                                underlineColor: .blue
                            ) { title, isSelected in
                                Text(title.uppercased())
                                   .font(.system(size: 14))
                                   .fontWeight(.semibold)
                                   .foregroundColor(isSelected ? .black : .gray)
                            }
                            .padding(.bottom, 5)
                            .padding(.top, geometry.safeAreaInsets.top)
                            .border(width: 0.3, edges: [.bottom], color: .black)
                            .background(tabsBackground)
                            if selectedIndex == 0 {
                                ChatsList(viewModel: singleViewModel)
                            }
                            else if selectedIndex == 1 {
                                ChatsList(viewModel: multipleViewModel)
                            }

                        }
                        .padding(.top, 15)
                    }
                }
                .navigationTransition()
            }
            .ignoresSafeArea(.container, edges: .top)
        }
    }

    private func findChat(route: RouteInformation) -> ChatListViewModelImpl? {
        if let parameter = route.parameters["chatId"] {
            return ChatListViewModelImpl(store: ChatStore(chatId: parameter))
        }
        return nil
    }
}

private struct SingleChatsView: View {
    let viewModel: ChatsListViewModelImpl
    var body: some View {
        ChatsList(viewModel: viewModel)
    }
}

private struct TabBar: View {
    var body: some View {
        HStack(alignment: .center) {
            TabBarButton(path: "/single", title: "Чаты")
            Spacer()
            TabBarButton(path: "/teams", title: "Команды")
        }
        .font(.system(size: 16))
        .buttonStyle(.plain)
        .frame(height: 70)
    }

    private func TabBarButton(path: String, title: String) -> some View {
            NavLink(to: path) { active in
                VStack {
                    Text(title)
                        .fontWeight(active ? .bold : .regular)
                }
                .border(width: 1, edges: [.bottom], color: active ? .blue : .clear)
            }
            .frame(maxWidth: .infinity)
        }
}

//struct MainView_Previews: PreviewProvider {
//    static var previews: some View {
//        MainView()
//    }
//}
